package com.food.ordering.system.kafka.config.data;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Added spring-boot-configuration-processor to root pom https://docs.spring.io/spring-boot/docs/2.6
 * .4/reference/html/configuration-metadata.html#appendix.configuration-metadata.annotation-processor
 * following IDE message
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "kafka-config")
public class KafkaConfigData
{
    private String bootstrapServers;
    private String schemaRegistryUrlKey;
    private String schemaRegistryUrl;
    private Integer numOfPartitions;
    private Short replicationFactor;


}
