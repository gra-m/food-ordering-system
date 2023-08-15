package com.food.ordering.system.kafka.producer.service.impl;

import com.food.ordering.system.kafka.producer.exception.KafkaProducerException;
import com.food.ordering.system.kafka.producer.service.KafkaProducer;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import javax.annotation.PreDestroy;
import java.io.Serializable;

@Slf4j
@Component
public class KafkaProducerImpl<K extends Serializable, V extends SpecificRecordBase> implements KafkaProducer<K, V> {

private final KafkaTemplate<K, V> kafkaTemplate;

/**
 * The kafka template for sending to Kafka producers, created as a bean in KafkaProducerConfig
 *
 * @param kafkaTemplate
 */
public KafkaProducerImpl(KafkaTemplate<K, V> kafkaTemplate) {
    this.kafkaTemplate = kafkaTemplate;
}

/**
 * A message is sent to topic asynchronously, the ListenableFuture has the SendResult added to it as a callback,
 * resolving
 * whenever it is responded to by the producer [barring timeout].
 *
 * @param topicName the topic the message is being sent to
 * @param key       the K Serializable of this KafkaProducer
 * @param message   the V SpecificRecordBase [Avro] of this KafkaProducer
 * @param callback  the callback, the result that will be returned on completion of the asynchronous ListenableFuture
 */
@Override
public void send(String topicName, K key, V message, ListenableFutureCallback<SendResult<K, V>> callback) {

    log.info("Sending message={} to topic ={}", message, topicName);

    try {
        ListenableFuture<SendResult<K, V>> kafkaResultFuture = kafkaTemplate.send(topicName, key, message);
        kafkaResultFuture.addCallback(callback);
    }
    catch( KafkaException e ) {
        log.error("Error on kafka producer with key: {}, message: {} and exception: {}", key, message, e.getMessage());
        throw new KafkaProducerException(String.format("Error on kafka producer with key: %s and message: %s",
        key,
        message), e);
    }

}


/**
 * @PreDestroy allows for cleanup code that will be run when an application is shutting down
 */
@PreDestroy
public void close() {
    if( kafkaTemplate != null ) {
        log.info("Closing kafka producer!");
        kafkaTemplate.destroy();
    }
}


}
