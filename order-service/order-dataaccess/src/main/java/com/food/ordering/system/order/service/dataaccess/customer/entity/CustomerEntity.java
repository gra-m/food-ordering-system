package com.food.ordering.system.order.service.dataaccess.customer.entity;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

/**
 * Used to check if a customer exists, so only UUID.
 * This is going to use a Postgresql 'Materialized View'
 *
 * @see <a href="https://www.postgresql.org/docs/current/rules-materializedviews.html">Materialized Views</a>
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "order_customer_m_view", schema = "customer")
@Entity
public class CustomerEntity
{

    @Id
    private UUID id;


}
