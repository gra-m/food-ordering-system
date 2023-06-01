package com.food.ordering.system.order.service.domain.ports.input.service;

import com.food.ordering.system.order.service.domain.event.OrderCreatedEvent;
import com.food.ordering.system.order.service.domain.ports.output.message.publisher.payment.OrderCreatedPaymentRequestMessagePublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
public class OrderCreatedEventApplicationListener {

private final OrderCreatedPaymentRequestMessagePublisher orderCreatedPaymentRequestMessagePublisher;


public OrderCreatedEventApplicationListener(OrderCreatedPaymentRequestMessagePublisher orderCreatedPaymentRequestMessagePublisher) {
      this.orderCreatedPaymentRequestMessagePublisher = orderCreatedPaymentRequestMessagePublisher;
}

/**
 * Given that this method is called from within an @Transactional method, this @TransactionalEventListener will only
 * proceed on its successful completion.
 * @param orderCreatedEvent  The event fired from the calling transactional method createOrder(CreateOrderCommand
 *                           createOrderCommand) {}
 */
@TransactionalEventListener
public void process(OrderCreatedEvent orderCreatedEvent) {
      orderCreatedPaymentRequestMessagePublisher.publish(orderCreatedEvent);

}

}
