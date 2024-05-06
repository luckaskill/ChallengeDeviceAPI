package com.challenge.service.device;

import lombok.SneakyThrows;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.core.io.ClassPathResource;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public interface PayloadUtils {

	static String getDevicePayload() {

		return getPayload("payloads/device.json");
	}

	static String getDevicesPayload() {

		return getPayload("payloads/devices.json");
	}

	@SneakyThrows
	private static String getPayload(String path) {

		ClassPathResource classPathResource = new ClassPathResource(path);
		InputStream inputStream = classPathResource.getInputStream();
		byte[] buffer = new byte[inputStream.available()];
		IOUtils.readFully(inputStream, buffer);
		return new String(buffer, StandardCharsets.UTF_8);
	}
}
