package com.challenge.dto.device.filter;

import java.time.LocalDateTime;

public record DeviceFilter(String brand, LocalDateTime from, LocalDateTime to) {

}
