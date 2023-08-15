package com.food.ordering.system.kafka.consumer;

import org.apache.avro.specific.SpecificRecordBase;

import java.util.List;

/**
 * Given that all consumer implementations will implement this interface
 *
 * @param <T> is ensured to be subtype of avro SpecificRecordBase
 */
public interface KafkaConsumer<T extends SpecificRecordBase> {

/**
 * @param messages   A list of given Type of messages
 * @param keys       A list of Longs that are keys
 * @param partitions A list of Integers that are the partitions
 * @param offsets    A list of Longs that are the offsets
 */
void receive(List<T> messages, List<String> keys, List<Integer> partitions, List<Long> offsets);


}
