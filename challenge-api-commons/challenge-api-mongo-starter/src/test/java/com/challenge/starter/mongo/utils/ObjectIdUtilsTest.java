package com.challenge.starter.mongo.utils;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ObjectIdUtilsTest {

	@Test
	void asDateTime() {

		LocalDateTime beforeCreation = LocalDateTime.now(ZoneOffset.UTC).minusSeconds(1);
		String id = new ObjectId().toHexString();
		LocalDateTime creationTime = ObjectIdUtils.asDateTime(id);
		LocalDateTime afterCreation = LocalDateTime.now(ZoneOffset.UTC).plusSeconds(1);

		assertTrue(beforeCreation.isBefore(creationTime));
		assertTrue(afterCreation.isAfter(creationTime));
	}
}