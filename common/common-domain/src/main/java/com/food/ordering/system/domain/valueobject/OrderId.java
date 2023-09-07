package com.food.ordering.system.domain.valueobject;

import java.util.UUID;

public class OrderId extends BaseId<UUID>
{

    public OrderId(UUID uuid)
    {
        super(uuid);
    }

    @Override
    public UUID getValue()
    {
        return super.getValue();
    }


}
