package com.challenge.dto.device;

import java.time.LocalDateTime;

public record DeviceDto(String id, String name, String brand, LocalDateTime createdAt, LocalDateTime lastUpdate) {

}
