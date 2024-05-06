package com.challenge.starter.mongo.utils;

import com.challenge.general.utils.DateUtils;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public interface ObjectIdUtils {

	String MAX_HEX_STRING_POSTFIX = "ffffffffffffffff";
	String MIN_HEX_STRING_POSTFIX = "0000000000000000";

	static LocalDateTime asDateTime(String id) {

		String timestampHex = id.substring(0, 8);
		long seconds = Long.parseLong(timestampHex, 16);
		return DateUtils.fromSeconds(seconds);
	}

	static ObjectId max(LocalDateTime time) {

		return asObjectId(time, MAX_HEX_STRING_POSTFIX);
	}

	static ObjectId min(LocalDateTime time) {

		return asObjectId(time, MIN_HEX_STRING_POSTFIX);
	}

	private static ObjectId asObjectId(LocalDateTime time, String hexPostfix) {

		long seconds = time.toInstant(ZoneOffset.UTC).toEpochMilli() / 1000;
		String hex = Long.toHexString(seconds) + hexPostfix;
		return new ObjectId(hex);
	}
}
