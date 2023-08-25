package com.food.ordering.system.payment.service.domain.valueobject;

import com.food.ordering.system.domain.valueobject.BaseId;
import java.util.UUID;

public class PaymentId extends BaseId<UUID> {

/**
 * @param value generic T in superclass will be set as whatever it is in this extending subclass.
 */
public PaymentId(UUID value) {
    super(value);
}


}
