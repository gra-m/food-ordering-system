package com.food.ordering.system.restaurant.service.dataaccess.restaurant.outbox.adapter;

import com.food.ordering.system.domain.valueobject.PaymentStatus;
import com.food.ordering.system.outbox.OutboxStatus;
import com.food.ordering.system.payment.service.domain.outbox.model.OrderOutboxMessage;
import com.food.ordering.system.payment.service.domain.ports.output.repository.OrderOutboxRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class OrderOutboxRepositoryImpl implements OrderOutboxRepository
{
    @Override
    public OrderOutboxMessage save(OrderOutboxMessage orderOutboxMessage)
    {
        return null;
    }

    @Override
    public Optional<List<OrderOutboxMessage>> findByTypeAndOutboxStatus(String type, OutboxStatus status)
    {
        return Optional.empty();
    }

    @Override
    public Optional<OrderOutboxMessage> findByTypeAndSagaIdAndPaymentStatusAndOutboxStatus(String type, UUID sagaId, PaymentStatus paymentStatus, OutboxStatus outboxStatus)
    {
        return Optional.empty();
    }

    @Override
    public void deleteByTypeAndOutboxStatus(String type, OutboxStatus status)
    {

    }
}
