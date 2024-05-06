package com.challenge.starter.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JacksonInitializer {

	private final ObjectMapper objectMapper;

	@EventListener(ContextRefreshedEvent.class)
	public void initializeObjectMapper() {

		objectMapper.registerModule(new IsoLocalDateTimeJacksonModule());
	}
}
