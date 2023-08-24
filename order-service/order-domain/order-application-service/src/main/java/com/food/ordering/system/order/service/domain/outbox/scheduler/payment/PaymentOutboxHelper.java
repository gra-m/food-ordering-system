package com.food.ordering.system.order.service.domain.outbox.scheduler.payment;

import com.food.ordering.system.order.service.domain.outbox.model.payment.OrderPaymentOutboxMessage;
import com.food.ordering.system.order.service.domain.ports.output.repository.PaymentOutboxRepository;
import com.food.ordering.system.outbox.OutboxStatus;
import com.food.ordering.system.saga.SagaStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
@Slf4j
@Component
public class PaymentOutboxHelper {
private final PaymentOutboxRepository paymentOutboxRepository;

public PaymentOutboxHelper(PaymentOutboxRepository paymentOutboxRepository) {
    this.paymentOutboxRepository = paymentOutboxRepository;
}

public Optional<List<OrderPaymentOutboxMessage>> getPaymentOutboxMessageByOutboxStatusAndSagaStatus(
OutboxStatus outboxStatus, SagaStatus... sagaStatus) {
    return null;
}


}
