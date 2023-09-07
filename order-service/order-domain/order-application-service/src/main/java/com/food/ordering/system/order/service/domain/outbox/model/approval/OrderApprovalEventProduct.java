package com.food.ordering.system.order.service.domain.outbox.model.approval;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

// to keep productId and quantity together
@Builder
@Getter
@AllArgsConstructor
public class OrderApprovalEventProduct
{
    @JsonProperty
    private String id;
    @JsonProperty
    private Integer quantity;
}
