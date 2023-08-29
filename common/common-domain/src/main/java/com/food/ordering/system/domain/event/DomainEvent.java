package com.food.ordering.system.domain.event;

/**
 * <h3>Used as marker interface for Domain Events that later enabled the implementation of a self-fire method for
 * inheriting envents</h3>
 * <p>
 * <p>
 * OrderCreatedEvent implements DomainEvent{Order}
 *
 * @param <T> is of type entity, the origin of the domain event
 */
public interface DomainEvent<T> {


}
