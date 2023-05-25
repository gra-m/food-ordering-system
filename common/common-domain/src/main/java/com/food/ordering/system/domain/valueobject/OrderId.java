package com.food.ordering.system.domain.valueobject;

import java.util.UUID;

/**
 * Here order id extends BaseId using UUID.
 *
 */
public class OrderId extends BaseId <UUID>{

      public OrderId(UUID uuid){
            super(uuid);
      }

}
