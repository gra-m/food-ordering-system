package com.food.ordering.system.order.service.messaging.mapper;

import com.food.ordering.system.kafka.order.avro.model.PaymentOrderStatus;
import com.food.ordering.system.kafka.order.avro.model.PaymentRequestAvroModel;
import com.food.ordering.system.order.service.domain.event.OrderCancelledEvent;
import com.food.ordering.system.order.service.domain.event.OrderCreatedEvent;
import com.food.ordering.system.order.service.domain.entity.Order;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Mapping received order events to PaymentRequestAvroModel objects for Kafka
 */
@Component
public class OrderMessagingDataMapper {


/**
 * Retrieve order, set new avro fields and set retrieved order fields, payment status set to PENDING.
 * <p>New into avroObject ==  set random Id, set Saga id payment [todo implement SAGA] status PENDING</p>
 * <p>The rest of the fields are from retrieved Order</p>
 *
 *
 * @param orderCreatedEvent containing order details
 * @return PaymentRequestAvroModel for Kafka with PENDING status
 */
public PaymentRequestAvroModel orderCreatedEventToPaymentRequestAvroModel(OrderCreatedEvent orderCreatedEvent) {

      Order order = orderCreatedEvent.getOrder();

      return PaymentRequestAvroModel.newBuilder()
          .setId(UUID.randomUUID().toString())
          .setSagaId("")
          .setCustomerId(order.getCustomerId().getValue().toString())
          .setOrderId(order.getId().getValue().toString())
          .setPrice(order.getPrice().getAmount())
          .setCreatedAt(orderCreatedEvent.getCreatedAt().toInstant())
          .setPaymentOrderStatus(PaymentOrderStatus.PENDING)
          .build();

}


/**
 * Retrieve order, set new avro fields and set retrieved order fields, payment status set to CANCELLED.
 * <p>New into avroObject ==  set random Id, set Saga id payment [to be implemented] status CANCELLED</p>
 * <p>The rest of the fields are from retrieved Order</p>
 *
 *
 * @param orderCancelledEvent containing order details
 * @return PaymentRequestAvroModel for Kafka with CANCELLED status
 */
public PaymentRequestAvroModel orderCanelledEventToPaymentRequestAvroModel(OrderCancelledEvent orderCancelledEvent) {

      Order order = orderCancelledEvent.getOrder();

      return PaymentRequestAvroModel.newBuilder()
                                    .setId(UUID.randomUUID().toString())
                                    .setSagaId("")
                                    .setCustomerId(order.getCustomerId().getValue().toString())
                                    .setOrderId(order.getId().getValue().toString())
                                    .setPrice(order.getPrice().getAmount())
                                    .setCreatedAt(orderCancelledEvent.getCreatedAt().toInstant())
                                    .setPaymentOrderStatus(PaymentOrderStatus.CANCELLED)
                                    .build();

}

}
