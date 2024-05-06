package com.challenge.service.device.service;

import com.challenge.dto.device.DeviceDto;
import com.challenge.dto.device.filter.DeviceFilter;

import java.util.List;

public interface DeviceService {

	List<DeviceDto> load(DeviceFilter filter);

	DeviceDto loadById(String id);

	String provision(DeviceDto device);

	void change(DeviceDto device);

	void forget(String id);
}
