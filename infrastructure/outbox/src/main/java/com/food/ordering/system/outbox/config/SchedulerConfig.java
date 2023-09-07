package com.food.ordering.system.outbox.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Configuration bean created that enables scheduling in application
 */
@Configuration
@EnableScheduling
public class SchedulerConfig
{

/*
Example only: Spring starter json is enough for our needs, but if extra configuration mapping is required:

@Bean
@Primary
public ObjectMapper objectMapper() {
return new ObjectMapper()
.setSerializationInclusion(JsonInclude.Include.NON_NULL)
.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
.registerModule(new JavaTimeModule());

}

This would fail if extra  jason properties were included
 */
}
