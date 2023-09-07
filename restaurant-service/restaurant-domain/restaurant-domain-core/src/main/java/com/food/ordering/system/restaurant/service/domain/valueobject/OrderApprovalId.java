package com.food.ordering.system.restaurant.service.domain.valueobject;

import com.food.ordering.system.domain.valueobject.BaseId;

import java.util.UUID;

public class OrderApprovalId extends BaseId<UUID>
{

    /**
     * @param value generic T will be set as whatever it is in the extending subclass.
     */
    public OrderApprovalId(UUID value)
    {
        super(value);
    }


}
