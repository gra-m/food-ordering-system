package com.food.ordering.system.order.service.domain.outbox.model.approval;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class OrderApprovalEventPayload {
  @JsonProperty private String orderId;
  @JsonProperty private String restaurantId;
  @JsonProperty private BigDecimal price;
  @JsonProperty private ZonedDateTime createdAt;
  @JsonProperty private String restaurantOrderStatus;
  @JsonProperty private List<OrderApprovalEventProduct> products;
}
