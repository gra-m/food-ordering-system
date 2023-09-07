package com.food.ordering.system.order.service.dataaccess.order.entity;


import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@IdClass(OrderEntityId.class)
@Table(name = "order_items")
@Entity
public class OrderItemEntity
{

    @Id
    private Long id;

    /**
     * Uniqueness guaranteeable only with primary and secondary keys together, presumably order item id is Long and not
     * UUID for another reason (poss simple numbering of items on a bill? why not int?)
     */
    @Id
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "ORDER_ID")
    private OrderEntity order;

    private UUID productId;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal subTotal;


    /**
     * Uses id and order as the primary key consists of these two fields.
     *
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderItemEntity that = (OrderItemEntity) o;
        return Objects.equals(id, that.id) && Objects.equals(order, that.order);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(id, order);
    }


}
