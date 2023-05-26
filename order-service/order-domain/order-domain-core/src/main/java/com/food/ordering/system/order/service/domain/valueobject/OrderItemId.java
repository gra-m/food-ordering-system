package com.food.ordering.system.order.service.domain.valueobject;

import com.food.ordering.system.domain.valueobject.BaseId;

/**
 * Order Item uniqueness is only important within context of the Order Aggregate, so it does not need UUID, just
 * a number starting at 1.
 */
public class OrderItemId extends BaseId<Long> {

public OrderItemId(Long value) {
      super(value);
}

}
