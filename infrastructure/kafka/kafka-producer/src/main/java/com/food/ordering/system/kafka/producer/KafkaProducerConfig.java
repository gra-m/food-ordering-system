package com.food.ordering.system.kafka.producer;

import com.food.ordering.system.kafka.config.data.KafkaConfigData;
import com.food.ordering.system.kafka.config.data.KafkaProducerConfigData;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

/**
 * @param <K> Serializable key
 * @param <V> SpecificRecordBase (Abstract Base Class of Avro record)
 */
@Configuration
public class KafkaProducerConfig<K extends Serializable, V extends SpecificRecordBase> {

private final KafkaConfigData kafkaConfigData;
private final KafkaProducerConfigData kafkaProducerConfigData;

public KafkaProducerConfig(KafkaConfigData kafkaConfigData, KafkaProducerConfigData kafkaProducerConfigData) {
    this.kafkaConfigData = kafkaConfigData;
    this.kafkaProducerConfigData = kafkaProducerConfigData;
}

/**
 * Static fields of org.apache.kafka.clients.producer.ProducerConfig are mapped as keys to the values of
 * kafka-config-data classes:
 * KafkaConfigData and
 * KafkaProducerConfigData.
 *
 * @return producerConfig Map[String, Object]
 */
@Bean
public Map<String, Object> producerConfig() {

    Map<String, Object> props = new HashMap<>();

    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfigData.getBootstrapServers());
    props.put(kafkaConfigData.getSchemaRegistryUrlKey(), kafkaConfigData.getSchemaRegistryUrl());
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, kafkaProducerConfigData.getKeySerializerClass());
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, kafkaProducerConfigData.getValueSerializerClass());
    props.put(ProducerConfig.BATCH_SIZE_CONFIG,
    kafkaProducerConfigData.getBatchSize() * kafkaProducerConfigData.getBatchSizeBoostFactor());
    props.put(ProducerConfig.LINGER_MS_CONFIG, kafkaProducerConfigData.getLingerMs());
    props.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, kafkaProducerConfigData.getCompressionType());
    props.put(ProducerConfig.ACKS_CONFIG, kafkaProducerConfigData.getAcks());
    props.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, kafkaProducerConfigData.getRequestTimeoutMs());
    props.put(ProducerConfig.RETRIES_CONFIG, kafkaProducerConfigData.getRetryCount());

    return props;
}

/**
 * package org.springframework.kafka.core; DefaultKafkaProducerFactory is used to return the default producer configs
 * set out in brought together in
 *
 * @return a ProducerFactory [K, V]
 * @see #producerConfig()
 */
@Bean
public ProducerFactory<K, V> producerFactory() {
    return new DefaultKafkaProducerFactory<>(producerConfig());
}

/**
 * The actual config template that will be sent to Kafka Producers, it is a wrapper class to send data to the Kafka
 * Cluster.
 *
 * @return KafkaTemplate[K, V] a wrapper class for sending data to the Kafka Cluster
 */
@Bean
public KafkaTemplate<K, V> kafkaTemplate() {
    return new KafkaTemplate<>(producerFactory());
}


}
