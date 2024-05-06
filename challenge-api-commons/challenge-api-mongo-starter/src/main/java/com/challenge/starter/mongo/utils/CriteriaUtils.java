package com.challenge.starter.mongo.utils;

import lombok.NonNull;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.query.Criteria;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

public interface CriteriaUtils {

	String ID = "id";

	static Criteria byId(@NonNull Object id) {

		return Criteria.where(ID).is(id);
	}

	static Optional<Criteria> equalTo(String field, Object value, boolean acceptNull) {

		if (Objects.isNull(value) && !acceptNull) {
			return Optional.empty();
		}
		return Optional.of(Criteria.where(field).is(value));
	}

	static Optional<Criteria> betweenObjectId(LocalDateTime from, LocalDateTime to) {

		boolean fromExists = Objects.nonNull(from);
		boolean toExists = Objects.nonNull(to);
		if (!fromExists && !toExists) {
			return Optional.empty();
		}
		Criteria criteria = Criteria.where(ID);
		if (fromExists) {
			ObjectId objectIdFrom = ObjectIdUtils.min(from);
			criteria.gte(objectIdFrom);
		}
		if (toExists) {
			ObjectId objectIdTo = ObjectIdUtils.max(to);
			criteria.lte(objectIdTo);
		}
		return Optional.of(criteria);
	}
}
