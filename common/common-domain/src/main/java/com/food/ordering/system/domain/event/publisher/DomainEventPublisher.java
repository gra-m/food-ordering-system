package com.food.ordering.system.domain.event.publisher;

import com.food.ordering.system.domain.event.DomainEvent;

/**
 * Given that DomainEvents of different types need to be published this interface provides a generic publish method for
 * publishing them.   Extends Domain event which is a marker interface for all Domain Events.
 *
 *
 * @param <T> OrderCancelled/Created/PaidEvent via
 */
public interface DomainEventPublisher<T extends DomainEvent> {
      void publish(T domainEvent);
}
