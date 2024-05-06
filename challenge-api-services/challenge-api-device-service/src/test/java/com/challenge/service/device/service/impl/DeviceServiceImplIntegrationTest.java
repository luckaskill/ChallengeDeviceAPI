package com.challenge.service.device.service.impl;

import com.challenge.dto.device.DeviceDto;
import com.challenge.dto.device.filter.DeviceFilter;
import com.challenge.general.exception.ChallengeInternalException;
import com.challenge.general.exception.UnitNotFoundException;
import com.challenge.general.utils.DateUtils;
import com.challenge.service.device.configuraion.DeviceServiceTestConfiguration;
import com.challenge.service.device.domain.document.DeviceDocument;
import com.challenge.service.device.exception.DeviceAlreadyExistsException;
import com.challenge.starter.mongo.MongoLiquibaseAutoConfiguration;
import com.challenge.starter.mongo.utils.ObjectIdUtils;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataMongoTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@Import({DeviceServiceTestConfiguration.class, MongoLiquibaseAutoConfiguration.class})
class DeviceServiceImplIntegrationTest {

	private static final DeviceDocument DEVICE_DOCUMENT_1
			= new DeviceDocument("Inone 10", "apple", DateUtils.fromIso("2020-12-31T23:59:59"));

	private static final DeviceDocument DEVICE_DOCUMENT_2
			= new DeviceDocument("Anrido x", "Samsung", DateUtils.fromIso("2020-12-31T23:59:59"));

	private static final DeviceDocument DEVICE_DOCUMENT_3
			= new DeviceDocument("Anrido y", "Samsung", DateUtils.fromIso("2020-12-31T23:59:59"));

	@Autowired
	private DeviceServiceImpl deviceService;

	@Autowired
	private MongoTemplate mongoTemplate;

	@Nested
	class DataChange {

		@BeforeEach
		public void cleanUp() {

			mongoTemplate.remove(new Query(), DeviceDocument.class);
		}

		@Test
		public void provisionTest() {

			LocalDateTime beforeProvision = LocalDateTime.now(ZoneOffset.UTC).minusSeconds(1);
			DeviceDto device = new DeviceDto(null, "Inone 10", "brando", null, null);
			String id = deviceService.provision(device);
			LocalDateTime afterProvision = LocalDateTime.now(ZoneOffset.UTC).plusSeconds(1);

			List<DeviceDocument> deviceDocuments = mongoTemplate.find(new Query(), DeviceDocument.class);
			assertEquals(1, deviceDocuments.size());

			DeviceDocument provisionedDevice = deviceDocuments.getFirst();

			assertEquals(id, provisionedDevice.getId());
			assertEquals(device.name(), provisionedDevice.getName());
			assertEquals(device.brand(), provisionedDevice.getBrand());
			LocalDateTime provisionTime = ObjectIdUtils.asDateTime(id);
			LocalDateTime savedProvisionTime = ObjectIdUtils.asDateTime(provisionedDevice.getId());
			assertEquals(provisionTime, savedProvisionTime);
			assertTrue(provisionTime.isAfter(beforeProvision), provisionTime + " is not after " + beforeProvision);
			assertTrue(provisionTime.isBefore(afterProvision), provisionTime + " is not before " + afterProvision);
			assertNull(provisionedDevice.getLastUpdate());
			assertEquals(device.brand(), provisionedDevice.getBrand());
		}

		@Test
		public void provisionWithExistentBrandAndNameTest() {

			mongoTemplate.save(DEVICE_DOCUMENT_1);
			DeviceDto device = new DeviceDto(null, DEVICE_DOCUMENT_1.getName(), DEVICE_DOCUMENT_1.getBrand(), null, null);
			ChallengeInternalException exception = assertThrows(DeviceAlreadyExistsException.class, () -> deviceService.provision(device));
			String exceptionMessage = exception.getMessage();
			assertTrue(exceptionMessage.contains(device.brand()));
			assertTrue(exceptionMessage.contains(device.name()));
			assertTrue(exceptionMessage.contains("already exists"));
			List<DeviceDocument> all = mongoTemplate.findAll(DeviceDocument.class);
			assertEquals(1, all.size());
		}

		@Test
		public void changeWithExistentBrandAndNameTest() {

			mongoTemplate.save(DEVICE_DOCUMENT_1);
			mongoTemplate.save(DEVICE_DOCUMENT_2);
			DeviceDto changeDevice = new DeviceDto(DEVICE_DOCUMENT_2.getId(), DEVICE_DOCUMENT_1.getName(), DEVICE_DOCUMENT_1.getBrand(), null, null);

			ChallengeInternalException exception = assertThrows(DeviceAlreadyExistsException.class, () -> deviceService.change(changeDevice));
			String exceptionMessage = exception.getMessage();
			assertTrue(exceptionMessage.contains(changeDevice.brand()));
			assertTrue(exceptionMessage.contains(changeDevice.name()));
			assertTrue(exceptionMessage.contains("already exists"));
			List<DeviceDocument> all = mongoTemplate.findAll(DeviceDocument.class);
			assertEquals(2, all.size());

			DeviceDocument firstDevice = all.getFirst();
			assertEquals(DEVICE_DOCUMENT_1, firstDevice);

			DeviceDocument secondDevice = all.getLast();
			assertEquals(DEVICE_DOCUMENT_2, secondDevice);
		}

		@Test
		public void changeWithNonExistentId() {

			String wrongId = "wrongId";
			DeviceDto device = new DeviceDto(wrongId, "Inone 10", "brando", null, null);
			ChallengeInternalException exception = assertThrows(UnitNotFoundException.class, () -> deviceService.change(device));
			String exceptionMessage = exception.getMessage();
			assertTrue(exceptionMessage.contains(wrongId));
			assertTrue(exceptionMessage.contains(DeviceDocument.class.getName()));
			List<DeviceDocument> all = mongoTemplate.findAll(DeviceDocument.class);
			assertEquals(0, all.size());
		}

		@Test
		public void forgetTest() {

			mongoTemplate.save(DEVICE_DOCUMENT_1);

			deviceService.forget(DEVICE_DOCUMENT_1.getId());

			List<DeviceDocument> deviceDocuments = mongoTemplate.find(new Query(), DeviceDocument.class);
			assertEquals(0, deviceDocuments.size());
		}

		@Test
		public void changeTest() {

			LocalDateTime beforeChange = LocalDateTime.now(ZoneOffset.UTC);
			mongoTemplate.save(DEVICE_DOCUMENT_1);
			DeviceDto deviceDto
					= new DeviceDto(DEVICE_DOCUMENT_1.getId(), "Inone 10", "brando", null, null);
			deviceService.change(deviceDto);
			LocalDateTime afterChange = LocalDateTime.now(ZoneOffset.UTC).plusSeconds(1);

			List<DeviceDocument> deviceDocuments = mongoTemplate.find(new Query(), DeviceDocument.class);
			assertEquals(1, deviceDocuments.size());

			DeviceDocument changedDevice = deviceDocuments.getFirst();
			assertEquals(deviceDto.id(), changedDevice.getId());
			assertEquals(deviceDto.name(), changedDevice.getName());
			assertEquals(deviceDto.brand(), changedDevice.getBrand());
			LocalDateTime lastUpdate = changedDevice.getLastUpdate();
			assertTrue(lastUpdate.isAfter(beforeChange), lastUpdate + " is not after " + beforeChange);
			assertTrue(lastUpdate.isBefore(afterChange), lastUpdate + " is not before " + afterChange);
		}
	}

	@Nested
	@TestInstance(TestInstance.Lifecycle.PER_CLASS)
	class LoadTest {

		@SneakyThrows
		@BeforeAll
		public void setUp() {

			mongoTemplate.save(DEVICE_DOCUMENT_1);
			TimeUnit.SECONDS.sleep(2);
			mongoTemplate.save(DEVICE_DOCUMENT_2);
			TimeUnit.SECONDS.sleep(2);
			mongoTemplate.save(DEVICE_DOCUMENT_3);
		}

		@Test
		public void loadAllTest() {

			List<DeviceDto> devices = deviceService.load(null);

			assertEquals(3, devices.size());
			DeviceDto device = devices.getFirst();
			assertEquals(DEVICE_DOCUMENT_1.getId(), device.id());
			assertEquals(DEVICE_DOCUMENT_1.getName(), device.name());
			assertEquals(DEVICE_DOCUMENT_1.getBrand(), device.brand());
			LocalDateTime createdAt = ObjectIdUtils.asDateTime(DEVICE_DOCUMENT_1.getId());
			assertEquals(createdAt, device.createdAt());
			assertEquals(DEVICE_DOCUMENT_1.getLastUpdate(), device.lastUpdate());

			DeviceDto device2 = devices.get(1);
			assertEquals(DEVICE_DOCUMENT_2.getId(), device2.id());
			assertEquals(DEVICE_DOCUMENT_2.getName(), device2.name());
			assertEquals(DEVICE_DOCUMENT_2.getBrand(), device2.brand());
			LocalDateTime createdAt2 = ObjectIdUtils.asDateTime(DEVICE_DOCUMENT_2.getId());
			assertEquals(createdAt2, device2.createdAt());
			assertEquals(DEVICE_DOCUMENT_2.getLastUpdate(), device2.lastUpdate());

			DeviceDto device3 = devices.getLast();
			assertEquals(DEVICE_DOCUMENT_3.getId(), device3.id());
			assertEquals(DEVICE_DOCUMENT_3.getName(), device3.name());
			assertEquals(DEVICE_DOCUMENT_3.getBrand(), device3.brand());
			LocalDateTime createdAt3 = ObjectIdUtils.asDateTime(DEVICE_DOCUMENT_3.getId());
			assertEquals(createdAt3, device3.createdAt());
			assertEquals(DEVICE_DOCUMENT_3.getLastUpdate(), device3.lastUpdate());
		}

		@Test
		public void loadByIdTest() {

			DeviceDto device = deviceService.loadById(DEVICE_DOCUMENT_1.getId());

			assertEquals(DEVICE_DOCUMENT_1.getId(), device.id());
			assertEquals(DEVICE_DOCUMENT_1.getName(), device.name());
			assertEquals(DEVICE_DOCUMENT_1.getBrand(), device.brand());
			LocalDateTime createdAt = ObjectIdUtils.asDateTime(DEVICE_DOCUMENT_1.getId());
			assertEquals(createdAt, device.createdAt());
			assertEquals(DEVICE_DOCUMENT_1.getLastUpdate(), device.lastUpdate());
		}

		@Test
		public void loadByWrongIdTest() {

			String wrongId = "wrongId";
			UnitNotFoundException exception = assertThrowsExactly(UnitNotFoundException.class, () -> deviceService.loadById(wrongId));

			String classReference = DeviceDocument.class.getName();
			String exceptionMessage = exception.getMessage();
			assertTrue(exceptionMessage.contains(wrongId));
			assertTrue(exceptionMessage.contains(classReference));
		}

		@Test
		public void loadByBrand() {

			List<DeviceDto> appleDevices = deviceService.load(new DeviceFilter("apple", null, null));
			assertEquals(1, appleDevices.size());

			DeviceDto appleDevice = appleDevices.getFirst();
			assertEquals(DEVICE_DOCUMENT_1.getId(), appleDevice.id());
			assertEquals(DEVICE_DOCUMENT_1.getName(), appleDevice.name());
			assertEquals(DEVICE_DOCUMENT_1.getBrand(), appleDevice.brand());
			LocalDateTime firstDeviceProvisionTime = ObjectIdUtils.asDateTime(DEVICE_DOCUMENT_1.getId());
			assertEquals(firstDeviceProvisionTime, appleDevice.createdAt());
			assertEquals(DEVICE_DOCUMENT_1.getLastUpdate(), appleDevice.lastUpdate());

			List<DeviceDto> samsungDevices = deviceService.load(new DeviceFilter("Samsung", null, null));
			assertEquals(2, samsungDevices.size());

			DeviceDto samsungDevice1 = samsungDevices.getFirst();
			assertEquals(DEVICE_DOCUMENT_2.getId(), samsungDevice1.id());
			assertEquals(DEVICE_DOCUMENT_2.getName(), samsungDevice1.name());
			assertEquals(DEVICE_DOCUMENT_2.getBrand(), samsungDevice1.brand());
			LocalDateTime secondDeviceProvisionTime = ObjectIdUtils.asDateTime(DEVICE_DOCUMENT_2.getId());
			assertEquals(secondDeviceProvisionTime, samsungDevice1.createdAt());
			assertEquals(DEVICE_DOCUMENT_2.getLastUpdate(), samsungDevice1.lastUpdate());

			DeviceDto samsungDevice2 = samsungDevices.getLast();
			assertEquals(DEVICE_DOCUMENT_3.getId(), samsungDevice2.id());
			assertEquals(DEVICE_DOCUMENT_3.getName(), samsungDevice2.name());
			assertEquals(DEVICE_DOCUMENT_3.getBrand(), samsungDevice2.brand());
			LocalDateTime thirdDeviceProvisionTime = ObjectIdUtils.asDateTime(DEVICE_DOCUMENT_3.getId());
			assertEquals(thirdDeviceProvisionTime, samsungDevice2.createdAt());
			assertEquals(DEVICE_DOCUMENT_3.getLastUpdate(), samsungDevice2.lastUpdate());
		}

		@Test
		public void loadByFromDateIncludeTest() {

			LocalDateTime secondDeviceProvisionTime = ObjectIdUtils.asDateTime(DEVICE_DOCUMENT_2.getId());
			List<DeviceDto> devices = deviceService.load(new DeviceFilter(null, secondDeviceProvisionTime, null));

			assertEquals(2, devices.size());

			DeviceDto device1 = devices.getFirst();
			assertEquals(DEVICE_DOCUMENT_2.getId(), device1.id());
			assertEquals(DEVICE_DOCUMENT_2.getName(), device1.name());
			assertEquals(DEVICE_DOCUMENT_2.getBrand(), device1.brand());
			assertEquals(secondDeviceProvisionTime, device1.createdAt());
			assertEquals(DEVICE_DOCUMENT_2.getLastUpdate(), device1.lastUpdate());

			DeviceDto device2 = devices.getLast();
			assertEquals(DEVICE_DOCUMENT_3.getId(), device2.id());
			assertEquals(DEVICE_DOCUMENT_3.getName(), device2.name());
			assertEquals(DEVICE_DOCUMENT_3.getBrand(), device2.brand());
			LocalDateTime thirdDeviceProvisionTime = ObjectIdUtils.asDateTime(DEVICE_DOCUMENT_3.getId());
			assertEquals(thirdDeviceProvisionTime, device2.createdAt());
			assertEquals(DEVICE_DOCUMENT_3.getLastUpdate(), device2.lastUpdate());
		}

		@Test
		public void loadByFromDateExcludeTest() {

			LocalDateTime secondDeviceProvisionTime = ObjectIdUtils.asDateTime(DEVICE_DOCUMENT_2.getId());
			List<DeviceDto> devices = deviceService.load(new DeviceFilter(null, secondDeviceProvisionTime.plusSeconds(1), null));

			assertEquals(1, devices.size());

			DeviceDto device2 = devices.getFirst();
			assertEquals(DEVICE_DOCUMENT_3.getId(), device2.id());
			assertEquals(DEVICE_DOCUMENT_3.getName(), device2.name());
			assertEquals(DEVICE_DOCUMENT_3.getBrand(), device2.brand());
			LocalDateTime thirdDeviceProvisionTime = ObjectIdUtils.asDateTime(DEVICE_DOCUMENT_3.getId());
			assertEquals(thirdDeviceProvisionTime, device2.createdAt());
			assertEquals(DEVICE_DOCUMENT_3.getLastUpdate(), device2.lastUpdate());
		}

		@Test
		public void loadByToDateIncludeTest() {

			LocalDateTime firstDeviceProvisionTime = ObjectIdUtils.asDateTime(DEVICE_DOCUMENT_1.getId());
			List<DeviceDto> devices = deviceService.load(new DeviceFilter(null, null, firstDeviceProvisionTime));

			assertEquals(1, devices.size());

			DeviceDto device = devices.getFirst();
			assertEquals(DEVICE_DOCUMENT_1.getId(), device.id());
			assertEquals(DEVICE_DOCUMENT_1.getName(), device.name());
			assertEquals(DEVICE_DOCUMENT_1.getBrand(), device.brand());
			assertEquals(firstDeviceProvisionTime, device.createdAt());
			assertEquals(DEVICE_DOCUMENT_1.getLastUpdate(), device.lastUpdate());
		}

		@Test
		public void loadByToDateExcludeTest() {

			LocalDateTime firstDeviceProvisionTime = ObjectIdUtils.asDateTime(DEVICE_DOCUMENT_1.getId());
			List<DeviceDto> devices = deviceService.load(new DeviceFilter(null, null, firstDeviceProvisionTime.minusSeconds(1)));

			assertEquals(0, devices.size());
		}

		@Test
		public void loadByBrandAndFromDateTest() {

			LocalDateTime firstDeviceProvisionTime = ObjectIdUtils.asDateTime(DEVICE_DOCUMENT_1.getId());
			List<DeviceDto> devices = deviceService.load(new DeviceFilter("Samsung", firstDeviceProvisionTime, null));

			assertEquals(2, devices.size());

			DeviceDto device2 = devices.getFirst();
			assertEquals(DEVICE_DOCUMENT_2.getId(), device2.id());
			assertEquals(DEVICE_DOCUMENT_2.getName(), device2.name());
			assertEquals(DEVICE_DOCUMENT_2.getBrand(), device2.brand());
			LocalDateTime secondDeviceProvisionTime = ObjectIdUtils.asDateTime(DEVICE_DOCUMENT_2.getId());
			assertEquals(secondDeviceProvisionTime, device2.createdAt());
			assertEquals(DEVICE_DOCUMENT_2.getLastUpdate(), device2.lastUpdate());

			DeviceDto device3 = devices.getLast();
			assertEquals(DEVICE_DOCUMENT_3.getId(), device3.id());
			assertEquals(DEVICE_DOCUMENT_3.getName(), device3.name());
			assertEquals(DEVICE_DOCUMENT_3.getBrand(), device3.brand());
			LocalDateTime thirdDeviceProvisionTime = ObjectIdUtils.asDateTime(DEVICE_DOCUMENT_3.getId());
			assertEquals(thirdDeviceProvisionTime, device3.createdAt());
			assertEquals(DEVICE_DOCUMENT_3.getLastUpdate(), device3.lastUpdate());
		}

		@Test
		public void loadByBrandAndToDateTest() {

			LocalDateTime thirdDeviceProvisionTime = ObjectIdUtils.asDateTime(DEVICE_DOCUMENT_2.getId());
			List<DeviceDto> devices = deviceService.load(new DeviceFilter("Samsung", null, thirdDeviceProvisionTime));

			assertEquals(1, devices.size());

			DeviceDto device = devices.getFirst();
			assertEquals(DEVICE_DOCUMENT_2.getId(), device.id());
			assertEquals(DEVICE_DOCUMENT_2.getName(), device.name());
			assertEquals(DEVICE_DOCUMENT_2.getBrand(), device.brand());
			LocalDateTime secondsDeviceProvisionTime = ObjectIdUtils.asDateTime(DEVICE_DOCUMENT_2.getId());
			assertEquals(secondsDeviceProvisionTime, device.createdAt());
			assertEquals(DEVICE_DOCUMENT_2.getLastUpdate(), device.lastUpdate());
		}

		@Test
		public void loadByPeriodTest() {

			LocalDateTime firstDeviceProvisionTime = ObjectIdUtils.asDateTime(DEVICE_DOCUMENT_1.getId());
			LocalDateTime secondDeviceProvisionTime = ObjectIdUtils.asDateTime(DEVICE_DOCUMENT_2.getId());
			List<DeviceDto> devices = deviceService.load(new DeviceFilter(null, firstDeviceProvisionTime, secondDeviceProvisionTime));

			assertEquals(2, devices.size());

			DeviceDto device1 = devices.getFirst();
			assertEquals(DEVICE_DOCUMENT_1.getId(), device1.id());
			assertEquals(DEVICE_DOCUMENT_1.getName(), device1.name());
			assertEquals(DEVICE_DOCUMENT_1.getBrand(), device1.brand());
			assertEquals(firstDeviceProvisionTime, device1.createdAt());
			assertEquals(DEVICE_DOCUMENT_1.getLastUpdate(), device1.lastUpdate());

			DeviceDto device2 = devices.getLast();
			assertEquals(DEVICE_DOCUMENT_2.getId(), device2.id());
			assertEquals(DEVICE_DOCUMENT_2.getName(), device2.name());
			assertEquals(DEVICE_DOCUMENT_2.getBrand(), device2.brand());
			assertEquals(secondDeviceProvisionTime, device2.createdAt());
			assertEquals(DEVICE_DOCUMENT_2.getLastUpdate(), device2.lastUpdate());
		}

		@Test
		public void loadByBrandAndPeriodTest() {

			LocalDateTime firstDeviceProvisionTime = ObjectIdUtils.asDateTime(DEVICE_DOCUMENT_1.getId());
			LocalDateTime thirdDeviceProvisionTime = ObjectIdUtils.asDateTime(DEVICE_DOCUMENT_3.getId());
			List<DeviceDto> devices = deviceService.load(new DeviceFilter("Samsung", firstDeviceProvisionTime, thirdDeviceProvisionTime));

			assertEquals(2, devices.size());

			DeviceDto device2 = devices.getFirst();
			assertEquals(DEVICE_DOCUMENT_2.getId(), device2.id());
			assertEquals(DEVICE_DOCUMENT_2.getName(), device2.name());
			assertEquals(DEVICE_DOCUMENT_2.getBrand(), device2.brand());
			LocalDateTime secondDeviceProvisionTime = ObjectIdUtils.asDateTime(DEVICE_DOCUMENT_2.getId());
			assertEquals(secondDeviceProvisionTime, device2.createdAt());
			assertEquals(DEVICE_DOCUMENT_2.getLastUpdate(), device2.lastUpdate());

			DeviceDto device3 = devices.getLast();
			assertEquals(DEVICE_DOCUMENT_3.getId(), device3.id());
			assertEquals(DEVICE_DOCUMENT_3.getName(), device3.name());
			assertEquals(DEVICE_DOCUMENT_3.getBrand(), device3.brand());
			assertEquals(thirdDeviceProvisionTime, device3.createdAt());
			assertEquals(DEVICE_DOCUMENT_3.getLastUpdate(), device3.lastUpdate());
		}
	}
}