package com.food.ordering.system.domain.event.publisher;

import com.food.ordering.system.domain.event.DomainEvent;

/**
 * Given that DomainEvents of different types need to be published this interface provides a generic publish method for
 * publishing them.   Extends Domain event which is a marker interface for all Domain Events.
 *
 * <p>As core-domain has a dependency to common-domain interface DomainEventPublisher offers a place to offer access to
 * different event publishers with generic type see PaymentCompleted/Cancelled/FailedEvent </p>
 *
 * @param <T> OrderCancelled/Created/PaidEvent via
 */
public interface DomainEventPublisher<T extends DomainEvent>
{

    void publish(T domainEvent);


}
