package com.food.ordering.system.payment.service.domain.valueobject;

import com.food.ordering.system.domain.valueobject.BaseId;
import java.util.UUID;

public class CreditEntryId extends BaseId<UUID> {

 /**
 * @param value generic T will be set as whatever it is in this extending subclass.
 */
public CreditEntryId(UUID value) {
    super(value);
}
}
