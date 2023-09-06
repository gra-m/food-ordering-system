package com.food.ordering.system.order.service.domain.outbox.scheduler.approval;

import com.food.ordering.system.order.service.domain.outbox.model.approval.OrderApprovalOutboxMessage;
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
public class RestaurantApprovalOutboxCleanerScheduler implements OutboxScheduler {
  private final ApprovalOutboxHelper approvalOutboxHelper;

  public RestaurantApprovalOutboxCleanerScheduler(final ApprovalOutboxHelper approvalOutboxHelper) {
    this.approvalOutboxHelper = approvalOutboxHelper;
  }

  @Override
  @Scheduled(cron = "@midnight")
  public void processOutboxMessage() {
    Optional<List<OrderApprovalOutboxMessage>> outboxMessagesResponse =
        approvalOutboxHelper.getApprovalOutboxMessagesByOutboxStatusAndSagaStatus(
            OutboxStatus.COMPLETED,
            SagaStatus.SUCCEEDED,
            SagaStatus.FAILED,
            SagaStatus.COMPENSATED);

    if (outboxMessagesResponse.isPresent()) {
      List<OrderApprovalOutboxMessage> outboxMessages = outboxMessagesResponse.get();

      log.info(
          "Received {} OrderApprovalOutboxMessage for clean-up. The payloads: {}",
          outboxMessages.size(),
          outboxMessages.stream()
              .map(orderApprovalOutboxMessage -> orderApprovalOutboxMessage.getPayload())
              .collect(Collectors.joining("\n")));

      approvalOutboxHelper.deleteApprovalOutboxMessageByOutboxStatusAndSagaStatus(
          OutboxStatus.COMPLETED, SagaStatus.SUCCEEDED, SagaStatus.FAILED, SagaStatus.COMPENSATED);
      log.info("{} OrderApprovalOutboxMessage deleted!", outboxMessages.size());
    }
  }
}