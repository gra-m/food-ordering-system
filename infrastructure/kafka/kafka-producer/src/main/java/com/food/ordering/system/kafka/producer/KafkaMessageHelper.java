package com.food.ordering.system.kafka.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.food.ordering.system.order.service.domain.exception.OrderDomainException;
import com.food.ordering.system.outbox.OutboxStatus;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.function.BiConsumer;

@Slf4j
@Component
public class KafkaMessageHelper
{
    private final ObjectMapper objectMapper;

    public KafkaMessageHelper(ObjectMapper objectMapper)
    {
        this.objectMapper = objectMapper;
    }

    public <T> T getOrderEventPayload(String payload, Class<T> outputType)
    {
        try {
            return objectMapper.readValue(payload, outputType);
        } catch (JsonProcessingException e) {
            log.error("Could not read {} object!", outputType.getName(), e);
            throw new OrderDomainException(String.format("Could not read %s object!\n%s", outputType.getName(), e));
        }
    }


    /**
     * Used by all Kafka producers and now set up to update outbox too.
     *
     * @param responseTopicName retrieved from orderServiceConfigData
     * @param avroModel         created in the publish method
     * @return a ListenableFutureCallback, currently just logging
     */
    public <T, U> ListenableFutureCallback<SendResult<String, T>> getKafkaCallback(String responseTopicName,
                                                                                   T avroModel,
                                                                                   U outboxMessage,
                                                                                   BiConsumer<U, OutboxStatus> outboxCallback,
                                                                                   String orderId,
                                                                                   String avroModelName)
    {

        return new ListenableFutureCallback<>()
        {
            @Override
            public void onFailure(Throwable ex)
            {
                log.error("Error while sending {} with message {} and outbox type {} to topic {} message:\n{}",
                        avroModelName,
                        avroModel.toString(),
                        outboxMessage.getClass().getName(),
                        responseTopicName,
                        ex.getMessage());

                // Here if kafka producer gets a failed response the outbox callback parameter provided by the
                // bi-consumer interface is called  with FAILED status.
                outboxCallback.accept(outboxMessage, OutboxStatus.FAILED);
            }

            @Override
            public void onSuccess(SendResult<String, T> result)
            {
                RecordMetadata metadata = result.getRecordMetadata();
                log.info("Received successful response from Kafka for order id: {}" + " Topic: {} Partition {} " +
                                "Offset: {} Timestamp: {}",
                        orderId,
                        metadata.topic(),
                        metadata.partition(),
                        metadata.offset(),
                        metadata.timestamp());
                // Here if kafka producer gets a success response the outbox callback parameter provided by the
                // bi-consumer interface is called  with COMPLETED status.
                outboxCallback.accept(outboxMessage, OutboxStatus.COMPLETED);
            }
        };
    }


}
