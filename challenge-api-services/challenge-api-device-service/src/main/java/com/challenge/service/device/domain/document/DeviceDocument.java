package com.challenge.service.device.domain.document;

import com.challenge.service.device.domain.DeviceMeta;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Data
@Document(collection = DeviceMeta.DEVICE_COLLECTION)
public class DeviceDocument {

	@Id
	String id;

	@Field(DeviceMeta.NAME)
	String name;

	@Indexed
	@Field(DeviceMeta.BRAND)
	String brand;

	@Field(DeviceMeta.LAST_UPDATE)
	LocalDateTime lastUpdate;

	public DeviceDocument(String name, String brand, LocalDateTime lastUpdate) {

		this.name = name;
		this.brand = brand;
		this.lastUpdate = lastUpdate;
	}
}
