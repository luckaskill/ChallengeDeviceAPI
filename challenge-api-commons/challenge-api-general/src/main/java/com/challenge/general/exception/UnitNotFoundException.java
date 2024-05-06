package com.challenge.general.exception;

public class UnitNotFoundException extends ChallengeInternalException {

	private static final String MESSAGE_TEMPLATE = "Cannot fine unit of type: %s by identifier: [%s]";

	public UnitNotFoundException(Object id, Class<?> type) {

		super(String.format(MESSAGE_TEMPLATE, type.getName(), id));
	}
}
