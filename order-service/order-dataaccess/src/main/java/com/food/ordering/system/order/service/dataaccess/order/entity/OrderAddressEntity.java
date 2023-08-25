package com.food.ordering.system.order.service.dataaccess.order.entity;


import java.util.Objects;
import java.util.UUID;
import javax.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "order_address")
@Entity
public class OrderAddressEntity {

@Id
private UUID id;

/**
 * Child CascadeType.ALL meaning that child OrderAddress will be deleted when an order is deleted.
 *
 * @JoinColumn to the order_address table will contain an ORDER_ID column, the primary key of order
 */
@OneToOne(cascade = CascadeType.ALL)
@JoinColumn(name = "ORDER_ID")
private OrderEntity order;

private String street;
private String postalCode;
private String city;


/**
 * Using only primary key
 *
 * @param o Object for comparison
 * @return boolean denoting whether objects are of same class and have the same UUID
 */
@Override
public boolean equals(Object o) {
    if( this == o ) return true;
    if( o == null || getClass() != o.getClass() ) return false;
    OrderAddressEntity that = ( OrderAddressEntity ) o;
    return Objects.equals(id, that.id);
}

@Override
public int hashCode() {
    return Objects.hash(id);
}


}
