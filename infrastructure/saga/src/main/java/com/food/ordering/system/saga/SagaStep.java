package com.food.ordering.system.saga;

import com.food.ordering.system.domain.event.DomainEvent;

/**
 * As can be seen S or U are always returned, so for an processed end event (the last step) an empty event has to be
 * sent back. Also for a firstStep rollback an EmptyEvent is returned. (common-domain [M].event.EmptyEvent
 *
 * @param <T> The data to be processed or rolledback
 * @param <S> The event to be returned after processing data
 * @param <U> The event to be returned after data rollback
 */
public interface SagaStep<T, S extends DomainEvent, U extends DomainEvent> {

/**
 * Processing transaction for SAGA step
 *
 * @param data the data to be processed
 * @return The DomainEvent returned after processing
 */
S process(T data);

/**
 * Rollback the action for this SAGA step
 *
 * @param data the data to be 'rolled back'
 * @return the Domain event returned after the rollback.
 */
U rollback(T data);


}
