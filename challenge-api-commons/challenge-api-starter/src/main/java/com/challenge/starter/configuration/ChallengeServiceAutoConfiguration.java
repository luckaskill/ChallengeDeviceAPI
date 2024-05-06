package com.challenge.starter.configuration;

import com.challenge.starter.Pool;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = Pool.BASE_PACKAGE)
public class ChallengeServiceAutoConfiguration {

}
