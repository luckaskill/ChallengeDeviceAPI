package com.challenge.starter.controller;

import com.challenge.general.exception.UnitNotFoundException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerAdvice {

	@ExceptionHandler(UnitNotFoundException.class)
	@SneakyThrows
	public void handlePersistentUnitNotFoundException(HttpServletResponse response) {

		response.sendError(HttpStatus.NOT_FOUND.value());
	}
}
