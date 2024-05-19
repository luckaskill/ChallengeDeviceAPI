package com.challenge.service.device.service.device.impl;

import com.challenge.dto.device.DeviceDto;
import com.challenge.dto.device.filter.DeviceFilter;
import com.challenge.general.exception.ChallengeInternalException;
import com.challenge.general.exception.EmptyPropertyException;
import com.challenge.general.utils.DateUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DeviceServiceImplUnitTest {

	private static DeviceServiceImpl deviceService;

	@BeforeAll
	public static void setUp() {

		DeviceServiceImplUnitTest.deviceService = new DeviceServiceImpl(null);
	}

	@Test
	public void loadWithToBeforeFromTest() {

		DeviceFilter mockFilter = new DeviceFilter(null, DateUtils.fromIso("2020-01-01T00:00:00"), DateUtils.fromIso("1990-01-01T00:00:00"));
		assertThrows(ChallengeInternalException.class, () -> deviceService.load(mockFilter));
	}

	@Test
	public void provisionWithNullBrandTest() {

		DeviceDto device = new DeviceDto("1", "Inone 10", null, null, null);
		Exception exception = assertThrowsExactly(EmptyPropertyException.class, () -> deviceService.provision(device));
		String expectedPart = "brand";
		String actualMessage = exception.getMessage();
		assertTrue(actualMessage.contains(expectedPart));
	}

	@Test
	public void provisionWithEmptyBrandTest() {

		DeviceDto device = new DeviceDto("1", "Inone 10", "", null, null);
		Exception exception = assertThrowsExactly(EmptyPropertyException.class, () -> deviceService.provision(device));
		String expectedPart = "brand";
		String actualMessage = exception.getMessage();
		assertTrue(actualMessage.contains(expectedPart));
	}

	@Test
	public void provisionWithNullNameTest() {

		DeviceDto device = new DeviceDto("1", null, "brando", null, null);
		Exception exception = assertThrowsExactly(EmptyPropertyException.class, () -> deviceService.provision(device));
		String expectedPart = "name";
		String actualMessage = exception.getMessage();
		assertTrue(actualMessage.contains(expectedPart));
	}

	@Test
	public void provisionWithEmptyNameTest() {

		DeviceDto device = new DeviceDto("1", "", "brando", null, null);
		Exception exception = assertThrowsExactly(EmptyPropertyException.class, () -> deviceService.provision(device));
		String expectedPart = "name";
		String actualMessage = exception.getMessage();
		assertTrue(actualMessage.contains(expectedPart));
	}

	@Test
	public void loadByNullIdTest() {

		assertThrowsExactly(NullPointerException.class, () -> deviceService.loadById(null));
	}

	@Test
	public void forgetByNullIdTest() {

		assertThrowsExactly(NullPointerException.class, () -> deviceService.forget(null));
	}

	@Test
	public void changeWithNullIdTest() {

		DeviceDto device = new DeviceDto(null, "Inone 10", "brando", null, null);
		EmptyPropertyException exception = assertThrowsExactly(EmptyPropertyException.class, () -> deviceService.change(device));
		String expectedPart = "id";
		String actualMessage = exception.getMessage();
		assertTrue(actualMessage.contains(expectedPart));
	}

	@Test
	public void changeWithNullBrandAndNameTest() {

		DeviceDto device = new DeviceDto("1", null, null, null, null);
		assertThrowsExactly(EmptyPropertyException.class, () -> deviceService.change(device));
	}

	@Test
	public void changeWithEmptyBrandTest() {

		DeviceDto device = new DeviceDto("1", "Inone 10", "", null, null);
		Exception exception = assertThrowsExactly(EmptyPropertyException.class, () -> deviceService.change(device));
		String expectedPart = "brand";
		String actualMessage = exception.getMessage();
		assertTrue(actualMessage.contains(expectedPart));
	}

	@Test
	public void changeWithEmptyNameTest() {

		DeviceDto device = new DeviceDto("1", "", "brando", null, null);
		Exception exception = assertThrowsExactly(EmptyPropertyException.class, () -> deviceService.change(device));
		String expectedPart = "name";
		String actualMessage = exception.getMessage();
		assertTrue(actualMessage.contains(expectedPart));
	}
}
