package com.food.ordering.system.order.service.dataaccess.order.entity;


import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "order_items")
@Entity
public class OrderItemEntity {

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



}
