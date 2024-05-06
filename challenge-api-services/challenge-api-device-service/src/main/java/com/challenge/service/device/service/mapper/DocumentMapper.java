package com.challenge.service.device.service.mapper;

import com.challenge.service.device.domain.document.DeviceDocument;

public interface DocumentMapper {

	static DeviceDocument toDeviceDocument(String name, String brand) {

		return new DeviceDocument(name, brand, null);
	}
}
