package com.challenge.general.exception;

public class EmptyPropertyException extends ChallengeInternalException {

	private static final String MESSAGE_TEMPLATE = "Required property [%s] is null or empty";

	public EmptyPropertyException(String property) {

		super(String.format(MESSAGE_TEMPLATE, property));
	}
}
