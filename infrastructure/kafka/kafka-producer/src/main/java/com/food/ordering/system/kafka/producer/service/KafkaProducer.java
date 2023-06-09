package com.food.ordering.system.kafka.producer.service;

import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.io.Serializable;


/**
 * Generic interface for sending data to Kafka topics [fixme]
 * @param <K> an Object that extends Serializable
 * @param <V> an Object extending Avro's SpecificRecordBase
 */
public interface KafkaProducer<K extends Serializable, V extends SpecificRecordBase> {

      void send(String topicName, K key, V message, ListenableFutureCallback<SendResult<K, V>> callback);
            

}
