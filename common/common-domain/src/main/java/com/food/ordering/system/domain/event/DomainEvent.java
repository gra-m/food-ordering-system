package com.food.ordering.system.domain.event;

/**
 * <h3>Will be generally used as: </h3>
 * T such as Order == the entity from which this Domain event originated
 * but instead of using BaseEntity, T allows flexibility.
 *
 * @param <T> is of type entity, the origin of the domain event
 */
public interface DomainEvent<T> {

}
