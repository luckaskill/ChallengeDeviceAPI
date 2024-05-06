package com.challenge.service.device.configuraion;

import com.challenge.starter.configuration.ChallengeServiceAutoConfiguration;
import com.challenge.starter.mongo.MongoLiquibaseAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackageClasses = {com.challenge.service.device.DeviceServiceMeta.class})
@EnableAutoConfiguration(exclude = {MongoLiquibaseAutoConfiguration.class, MongoAutoConfiguration.class, ChallengeServiceAutoConfiguration.class})
public class DeviceServiceTestConfiguration {

}
