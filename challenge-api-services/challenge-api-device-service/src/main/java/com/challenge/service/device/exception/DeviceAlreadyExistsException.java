package com.challenge.service.device.exception;

import com.challenge.general.exception.ChallengeInternalException;

public class DeviceAlreadyExistsException extends ChallengeInternalException {

	private static final String MESSAGE_TEMPLATE = "Device [%s:%s] already exists";

	public DeviceAlreadyExistsException(String brand, String name, Throwable cause) {

		super(String.format(MESSAGE_TEMPLATE, brand, name), cause);
	}
}
