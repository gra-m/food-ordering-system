package com.food.ordering.system.order.service.dataaccess.order.entity;


import lombok.*;

import java.io.Serializable;
import java.util.Objects;

/**
 * EntityId class must be serializeable for persistence
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderEntityId implements Serializable {

private Long id;
private OrderEntity order;

@Override
public boolean equals(Object o) {
    if( this == o ) return true;
    if( o == null || getClass() != o.getClass() ) return false;
    OrderEntityId that = ( OrderEntityId ) o;
    return Objects.equals(id, that.id) && Objects.equals(order, that.order);
}

@Override
public int hashCode() {
    return Objects.hash(id, order);
}


}
