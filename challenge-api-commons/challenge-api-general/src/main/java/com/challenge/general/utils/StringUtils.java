package com.challenge.general.utils;

import java.util.Objects;

public interface StringUtils {

	static boolean nullOrEmpty(String id) {

		return Objects.isNull(id) || id.isBlank();
	}
}
