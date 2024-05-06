package com.challenge.starter.jackson;

import com.fasterxml.jackson.databind.module.SimpleModule;

import java.time.LocalDateTime;

public class IsoLocalDateTimeJacksonModule extends SimpleModule {

	public IsoLocalDateTimeJacksonModule() {

		addSerializer(LocalDateTime.class, new IsoLocalDateTimeSerializer());
	}
}
