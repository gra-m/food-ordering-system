package com.food.ordering.system.saga;


/**
 * As can be seen S or U are always returned, so for an processed end event (the last step) an empty event has to be
 * sent back. Also for a firstStep rollback an EmptyEvent is returned. (common-domain [M].event.EmptyEvent
 *
 * @param <T> The data to be processed or rolledback
 */
public interface SagaStep<T>  {

/**
 * Processing transaction for SAGA step
 *
 * @param data the data to be processed
 * @return The DomainEvent returned after processing
 */
void process(T data);

/**
 * Rollback the action for this SAGA step
 *
 * @param data the data to be 'rolled back'
 * @return the Domain event returned after the rollback.
 */
void rollback(T data);


}
