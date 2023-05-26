package com.food.ordering.system.domain.event;

/**
 * <h3>Used as marker interface for Domain Events: </h3>
 *
 * OrderCreatedEvent implements DomainEvent{Order}
 *
 * @param <T> is of type entity, the origin of the domain event
 */
public interface DomainEvent<T> {

}
