package com.challenge.service.device.service.device.impl;

import com.challenge.dto.device.DeviceDto;
import com.challenge.dto.device.filter.DeviceFilter;
import com.challenge.general.exception.ChallengeInternalException;
import com.challenge.general.exception.EmptyPropertyException;
import com.challenge.general.exception.UnitNotFoundException;
import com.challenge.general.utils.DateUtils;
import com.challenge.general.utils.StringUtils;
import com.challenge.service.device.domain.DeviceMeta;
import com.challenge.service.device.domain.document.DeviceDocument;
import com.challenge.service.device.exception.DeviceAlreadyExistsException;
import com.challenge.service.device.service.device.DeviceService;
import com.challenge.service.device.service.mapper.DocumentMapper;
import com.challenge.service.device.service.mapper.DtoMapper;
import com.challenge.starter.mongo.utils.CriteriaUtils;
import com.challenge.starter.mongo.utils.QueryUtils;
import com.mongodb.client.result.UpdateResult;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DeviceServiceImpl implements DeviceService {

	private final MongoTemplate mongoTemplate;

	@Override
	public List<DeviceDto> load(DeviceFilter filter) {

		validate(filter);

		Query query = asQuery(filter);
		return mongoTemplate.find(query, DeviceDocument.class).stream()
				.map(DtoMapper::toDeviceDto)
				.toList();
	}

	@Override
	public DeviceDto loadById(String id) {

		Query query = QueryUtils.byId(id);
		DeviceDocument deviceDocuments = mongoTemplate.findOne(query, DeviceDocument.class);
		if (Objects.isNull(deviceDocuments)) {
			throw new UnitNotFoundException(id, DeviceDocument.class);
		}
		return DtoMapper.toDeviceDto(deviceDocuments);
	}

	@Override
	public String provision(DeviceDto device) {

		validateForPersist(device);

		DeviceDocument deviceDocument = DocumentMapper.toDeviceDocument(device.name(), device.brand());
		try {
			mongoTemplate.save(deviceDocument);
			return deviceDocument.getId();
		} catch (DuplicateKeyException e) {
			throw new DeviceAlreadyExistsException(device.brand(), device.name(), e);
		}
	}

	@Override
	public void change(DeviceDto device) {

		validateForChange(device);

		Update update = new Update();
		Optional.ofNullable(device.brand()).ifPresent(brand -> update.set(DeviceMeta.BRAND, brand));
		Optional.ofNullable(device.name()).ifPresent(name -> update.set(DeviceMeta.NAME, name));
		update.set(DeviceMeta.LAST_UPDATE, DateUtils.now());

		Query query = QueryUtils.byId(device.id());
		try {
			UpdateResult updateResult = mongoTemplate.updateFirst(query, update, DeviceDocument.class);
			if (updateResult.getMatchedCount() == 0) {
				throw new UnitNotFoundException(device.id(), DeviceDocument.class);
			}
		} catch (DuplicateKeyException e) {
			throw new DeviceAlreadyExistsException(device.brand(), device.name(), e);
		}
	}

	@Override
	public void forget(String id) {

		Query query = QueryUtils.byId(id);
		mongoTemplate.remove(query, DeviceDocument.class);
	}

	private static Query asQuery(DeviceFilter filter) {

		return Optional.ofNullable(filter)
				.map(__ -> {
					Query query = new Query();
					CriteriaUtils.equalTo(DeviceMeta.BRAND, filter.brand(), false).ifPresent(query::addCriteria);
					CriteriaUtils.betweenObjectId(filter.from(), filter.to()).ifPresent(query::addCriteria);
					return query;
				}).orElse(QueryUtils.EMPTY_QUERY);
	}

	private static void validateForPersist(DeviceDto device) {

		String brand = device.brand();
		if (StringUtils.nullOrEmpty(brand)) {
			throw new EmptyPropertyException("brand");
		}
		String name = device.name();
		if (StringUtils.nullOrEmpty(name)) {
			throw new EmptyPropertyException("name");
		}
	}

	private static void validateForChange(DeviceDto device) {

		if (StringUtils.nullOrEmpty(device.id())) {
			throw new EmptyPropertyException("id");
		}
		if (StringUtils.nullOrEmpty(device.brand())) {
			throw new EmptyPropertyException("brand");
		}
		if (StringUtils.nullOrEmpty(device.name())) {
			throw new EmptyPropertyException("name");
		}
	}

	private static void validate(DeviceFilter filter) {

		if (Objects.nonNull(filter)) {
			LocalDateTime from = filter.from();
			LocalDateTime to = filter.to();
			if (Objects.nonNull(from) && Objects.nonNull(to) && from.isAfter(to)) {
				throw new ChallengeInternalException("'from' must be before 'to'");
			}
		}
	}
}
