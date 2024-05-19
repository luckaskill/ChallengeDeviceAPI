package com.challenge.service.device.controller;

import com.challenge.dto.device.DeviceDto;
import com.challenge.dto.device.filter.DeviceFilter;
import com.challenge.service.device.service.device.DeviceService;
import com.challenge.starter.Navigation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Navigation.DEVICE)
@RequiredArgsConstructor
public class DeviceController {

	private final DeviceService deviceService;

	@GetMapping
	public ResponseEntity<?> loadDevices(DeviceFilter filter) {

		var devices = deviceService.load(filter);
		return ResponseEntity.ok(devices);
	}

	@GetMapping(Navigation.BY_ID)
	public ResponseEntity<?> loadDeviceById(@PathVariable(Navigation.ID) String id) {

		DeviceDto device = deviceService.loadById(id);
		return ResponseEntity.ok(device);
	}

	@PostMapping
	public ResponseEntity<?> provision(@RequestBody DeviceDto device) {

		String id = deviceService.provision(device);
		return ResponseEntity.ok(id);
	}

	@PutMapping
	public ResponseEntity<?> change(@RequestBody DeviceDto device) {

		deviceService.change(device);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping(Navigation.BY_ID)
	public ResponseEntity<?> forget(@PathVariable(Navigation.ID) String id) {

		deviceService.forget(id);
		return ResponseEntity.ok().build();
	}

}
