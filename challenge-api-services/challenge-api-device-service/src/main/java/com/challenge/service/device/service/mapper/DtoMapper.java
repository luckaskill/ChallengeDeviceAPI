package com.challenge.service.device.service.mapper;

import com.challenge.dto.device.DeviceDto;
import com.challenge.service.device.domain.document.DeviceDocument;
import com.challenge.starter.mongo.utils.ObjectIdUtils;

import java.util.Objects;

public interface DtoMapper {

	static DeviceDto toDeviceDto(DeviceDocument device) {

		if (Objects.isNull(device)) {
			return null;
		}
		String id = device.getId();
		return new DeviceDto(id, device.getName(), device.getBrand(), ObjectIdUtils.asDateTime(id), device.getLastUpdate());
	}
}
