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

/**
 * KafkaProducer send method is asynchronous, a response will not be immediately available.
 *
 * @param topicName the topic that the data is to be sent to
 * @param key eg. a String format of a UUID orderId
 * @param message must be a message that kafka can consume currently these are Avro-Models
 * @param callback asynch call-back method that will be called when kafka producer receives a response from the kafka
 *                cluster
 */
void send(String topicName, K key, V message, ListenableFutureCallback<SendResult<K, V>> callback);
            

}
