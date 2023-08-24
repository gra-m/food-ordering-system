package com.food.ordering.system.order.service.domain.outbox.model.payment;

import com.food.ordering.system.domain.valueobject.OrderStatus;
import com.food.ordering.system.outbox.OutboxStatus;
import com.food.ordering.system.saga.SagaStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.ZonedDateTime;
import java.util.UUID;

// Maps directly to order-container init-schema.sql CREATE TABLE "order".payment_outbox
@Getter
@Builder
@AllArgsConstructor
public class OrderPaymentOutboxMessage {
  private UUID id;
  private UUID sagaId;
  private ZonedDateTime createdAt;
  private ZonedDateTime processedAt;
  private String type;
  private String payload; // json payload requires mapper
  private SagaStatus sagaStatus;
  private OrderStatus orderStatus;
  private OutboxStatus outboxStatus;
  private int version;

  // Set only when outbox message is processed
  public void setProcessedAt(ZonedDateTime processedAt) {
    this.processedAt = processedAt;
  }

  // Set only when outbox message is processed
  public void setSagaStatus(SagaStatus sagaStatus) {
    this.sagaStatus = sagaStatus;
  }

  // Set only when outbox message is processed
  public void setOrderStatus(OrderStatus orderStatus) {
    this.orderStatus = orderStatus;
  }

  public void setOutboxStatus(OutboxStatus outboxStatus) {
    this.outboxStatus = outboxStatus;
  }
}