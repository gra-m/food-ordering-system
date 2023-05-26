package com.food.ordering.system.order.service.domain;

import com.food.ordering.system.order.service.domain.entity.Order;
import com.food.ordering.system.order.service.domain.entity.Restaurant;
import com.food.ordering.system.order.service.domain.event.OrderCancelledEvent;
import com.food.ordering.system.order.service.domain.event.OrderCreatedEvent;
import com.food.ordering.system.order.service.domain.event.OrderPaidEvent;
import java.util.List;

/**
 * Notes on choices here:
 *
 * <p>chooses to return domain events from Domain service (bouncing them out of domain core rather than handling them
 * here)
 * <p>The event firing process will be handled by the calling application service. This choice is because:
 * Before firing an event the data should first have been persisted, and he does not want that persistence code held within
 * the domain code. Think about the dependencies! This follows his mantra of domain-core only containing business logic.
 *
 * <p>Domain layer should not know about how to fire an event it only creates and returns them after running the BL.
 * <br>
 * <h3>Extra Design Philosophy</h3>
 * <p>Although it is considered natural to have entities return domain events in DDD he chooses to
 * use a DomainService so that the Entities are never directly accessed from the application service.
 * Under normal Domain Driven Design principles it would be fine to do this and you would only 'need' a Domain service
 * if there was code required that didn't fit elsewhere or you were handling multiple aggregates.</p>
 *
 * ALSO NOTE:
 * Although logging is available from the Base pom only this domain service uses logging, not the domain core. Another reason?
 */
public interface OrderDomainService {

OrderCreatedEvent validateAndInitiateOrder(Order order, Restaurant restaurant);

OrderPaidEvent payOrder(Order order);

void approveOrder(Order order);

OrderCancelledEvent cancelOrderPayment(Order order, List<String> failureMessages);

void cancelOrder(Order order, List<String> failureMessages);
}
