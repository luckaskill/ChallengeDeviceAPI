package com.challenge.service.device.controller;

import com.challenge.dto.device.DeviceDto;
import com.challenge.dto.device.filter.DeviceFilter;
import com.challenge.general.exception.UnitNotFoundException;
import com.challenge.general.utils.DateUtils;
import com.challenge.service.device.PayloadUtils;
import com.challenge.service.device.configuraion.DeviceServiceTestConfiguration;
import com.challenge.service.device.domain.document.DeviceDocument;
import com.challenge.service.device.service.DeviceService;
import com.challenge.starter.Navigation;
import com.challenge.starter.controller.ControllerAdvice;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = DeviceServiceTestConfiguration.class)
@Import(ControllerAdvice.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class DeviceControllerTest {

	@SpyBean
	private DeviceService deviceService;

	@MockBean
	private MongoTemplate mongoTemplate;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@SneakyThrows
	@Test
	public void loadDevicesTest() {

		String devicePayload = PayloadUtils.getDevicesPayload();
		var devices = objectMapper.readValue(devicePayload, new TypeReference<List<DeviceDto>>() {

		});
		doReturn(devices).when(deviceService).load(any());

		mockMvc.perform(get(Navigation.DEVICE)
						.param("brand", "Samsung")
						.param("from", "2021-01-01T00:00:00")
						.param("to", "2021-12-31T23:59:59"))

				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(devices.size())))
				.andExpect(jsonPath("$[0].id").value("1"))
				.andExpect(jsonPath("$[0].name").value("Inone 10"))
				.andExpect(jsonPath("$[0].brand").value("Elppa"))
				.andExpect(jsonPath("$[0].createdAt").value("2020-01-01T00:00:00"))
				.andExpect(jsonPath("$[0].lastUpdate").value("2020-01-31T00:00:00"))

				.andExpect(jsonPath("$[1].id").value("2"))
				.andExpect(jsonPath("$[1].name").value("Droidan 10"))
				.andExpect(jsonPath("$[1].brand").value("Droidan"))
				.andExpect(jsonPath("$[1].createdAt").value("2020-01-01T00:00:00"))
				.andExpect(jsonPath("$[1].lastUpdate").value("2020-01-31T23:59:59"))

				.andExpect(jsonPath("$[2].id").value("3"))
				.andExpect(jsonPath("$[2].name").value("Inone 1020"))
				.andExpect(jsonPath("$[2].brand").value("Elppa"))
				.andExpect(jsonPath("$[2].createdAt").value("2020-02-01T23:59:59"))
				.andExpect(jsonPath("$[2].lastUpdate").value("2020-02-29T00:00:00"))

				.andDo(print());

		LocalDateTime from = DateUtils.fromIso("2021-01-01T00:00:00");
		LocalDateTime to = DateUtils.fromIso("2021-12-31T23:59:59");
		DeviceFilter filter = new DeviceFilter("Samsung", from, to);

		verify(deviceService).load(filter);
		verifyNoMoreInteractions(deviceService);
	}

	@SneakyThrows
	@Test
	void loadByWrongTimeFormatTest() {

		mockMvc.perform(get(Navigation.DEVICE)
						.param("from", "2021/01/01T00:00:00"))
				.andExpect(status().isBadRequest())
				.andDo(print());

		verifyNoMoreInteractions(deviceService);
	}

	@SneakyThrows
	@Test
	void loadNonExistentDeviceTest() {

		String wrongId = "wrongId";
		doThrow(new UnitNotFoundException(wrongId, DeviceDocument.class))
				.when(deviceService)
				.loadById(wrongId);

		mockMvc.perform(get(Navigation.DEVICE + "/" + wrongId))
				.andExpect(status().isNotFound())
				.andDo(print());

		verify(deviceService).loadById(wrongId);
		verifyNoMoreInteractions(deviceService);
	}

	@SneakyThrows
	@Test
	void loadDeviceByIdTest() {

		String devicePayload = PayloadUtils.getDevicePayload();
		var device = objectMapper.readValue(devicePayload, DeviceDto.class);
		String id = "1";

		doReturn(device).when(deviceService).loadById(id);

		mockMvc.perform(get(Navigation.DEVICE + "/" + id))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(id))
				.andExpect(jsonPath("$.name").value("Inone 10"))
				.andExpect(jsonPath("$.brand").value("Elppa"))
				.andExpect(jsonPath("$.createdAt").value("2020-01-01T00:00:00"))
				.andExpect(jsonPath("$.lastUpdate").value("2020-12-31T23:59:59"))
				.andDo(print());

		verify(deviceService).loadById(id);
		verifyNoMoreInteractions(deviceService);
	}

	@SneakyThrows
	@Test
	void provisionTest() {

		String id = "1";
		DeviceDto device = new DeviceDto(id, "Inone 10", "Elppa", DateUtils.fromIso("2020-01-01T00:00:00"), DateUtils.fromIso("2020-12-31T23:59:59"));
		when(deviceService.provision(device)).thenReturn(id);

		mockMvc.perform(post(Navigation.DEVICE)
						.contentType("application/json")
						.content(objectMapper.writeValueAsString(device)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").value(id))
				.andDo(print());

		verify(deviceService).provision(device);
		verifyNoMoreInteractions(deviceService);
	}

	@SneakyThrows
	@Test
	void changeTest() {

		doNothing().when(deviceService).change(any());

		DeviceDto device = new DeviceDto("1", "Inone 10", "Elppa", DateUtils.fromIso("2020-01-01T00:00:00"), DateUtils.fromIso("2020-12-31T23:59:59"));
		mockMvc.perform(put(Navigation.DEVICE)
						.contentType("application/json")
						.content(objectMapper.writeValueAsString(device)))
				.andExpect(status().isOk())
				.andDo(print());

		verify(deviceService).change(device);
		verifyNoMoreInteractions(deviceService);
	}

	@SneakyThrows
	@Test
	void forgetTest() {

		doNothing().when(deviceService).forget(any());

		String id = "1";
		mockMvc.perform(delete(Navigation.DEVICE + "/" + id))
				.andExpect(status().isOk())
				.andDo(print());

		verify(deviceService).forget(id);
		verifyNoMoreInteractions(deviceService);
	}
}