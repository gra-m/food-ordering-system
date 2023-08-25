package com.food.ordering.system.order.service.domain.outbox.scheduler.payment;

import com.food.ordering.system.order.service.domain.outbox.model.payment.OrderPaymentOutboxMessage;
import com.food.ordering.system.outbox.OutboxScheduler;
import com.food.ordering.system.outbox.OutboxStatus;
import com.food.ordering.system.saga.SagaStatus;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PaymentOutboxCleanerScheduler implements OutboxScheduler {
  private final PaymentOutboxHelper paymentOutboxHelper;

  public PaymentOutboxCleanerScheduler(PaymentOutboxHelper paymentOutboxHelper) {
    this.paymentOutboxHelper = paymentOutboxHelper;
  }

  @Override
  @Scheduled(cron = "@midnight")
  public void processOutboxMessage() {
    Optional<List<OrderPaymentOutboxMessage>> outboxMessageResponse =
        paymentOutboxHelper.getPaymentOutboxMessageByOutboxStatusAndSagaStatus(
            OutboxStatus.COMPLETED,
            SagaStatus.SUCCEEDED,
            SagaStatus.FAILED,
            SagaStatus.COMPENSATED);
    if (outboxMessageResponse.isPresent()) {
      List<OrderPaymentOutboxMessage> outboxMessages = outboxMessageResponse.get();
      log.info(
          "Received {} orderPaymentOutboxMessage for clean-up. The Payloads: {}",
          outboxMessages.size(),
          outboxMessages.stream()
              .map(outboxMessage -> outboxMessage.getPayload())
              .collect(Collectors.joining("\n")));
      paymentOutboxHelper.deletePaymentOutboxMessageByOutboxStatusAndSagaStatus(
          OutboxStatus.COMPLETED, SagaStatus.SUCCEEDED, SagaStatus.FAILED, SagaStatus.COMPENSATED);
      log.info(
          "{} OrderPaymentOutboxMessage deleted!", outboxMessages.size());
    }
  }
}
