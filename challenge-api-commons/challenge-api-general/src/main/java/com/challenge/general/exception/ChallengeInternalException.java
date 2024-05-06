package com.challenge.general.exception;

public class ChallengeInternalException extends RuntimeException {

	public ChallengeInternalException(String message) {

		super(message);
	}

	public ChallengeInternalException(String message, Throwable cause) {

		super(message, cause);
	}
}
