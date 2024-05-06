package com.challenge.general.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public interface DateUtils {

	String ISO_DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss[.SSS]";
	DateTimeFormatter ISO_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(ISO_DATE_TIME_FORMAT);

	static LocalDateTime fromSeconds(long seconds) {

		Instant instant = Instant.ofEpochSecond(seconds);
		return LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
	}

	static LocalDateTime now() {

		return LocalDateTime.now(ZoneOffset.UTC);
	}

	static String iso(LocalDateTime ldt) {

		return ISO_DATE_TIME_FORMATTER.format(ldt);
	}

	static LocalDateTime fromIso(String iso) {

		return LocalDateTime.parse(iso, ISO_DATE_TIME_FORMATTER);
	}
}
