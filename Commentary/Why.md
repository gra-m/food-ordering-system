# Why?

## SAGA
> Working @ 34f8245b713a116f7bec4297e87c095999892c98
> 
Rollback transactions if criteria fails.
Example -> 
- product not available when Restaurant consulted
- cost of order exceeds customer credit (simple, internal payment failure scenario)
Saga ensures that each services database is returned to the state it was in before the action was attempted:

### Note the below does not use classes involved and is happy path only:
- [OrderService (SAGA Co-ordinator)] OrderCreatedPaymentRequest -> <- PaymentCompletedResponse (OK with payment) [Payment Service]
- [OrderService (SAGA Co-ordinator)] OrderPaidRestaurantApprovalRequest -> <- OrderApprovedResponse (OK with payment) [Payment Service]

<i> if anything goes wrong saga rollback takes care of it </i>

```java
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


```
## OUTBOX
- Because the messaging and ACID transaction are separate, and so can/could fail separately.
- Database changes are confirmed and only then are the Domain events (messages) fired, this is good, but..
- What if the publisher of the event fails, or the consumer of that published event fails before processing the required business logic?
- Well, Saga would not be able to continue and the system would be left in an inconsistent state.

### Outbox uses:
- local ACID transactions to allow for (eventual) consistent distributed transactions.
- automatic persistence of events to a local database with ACID transactions
- local microservices OUTBOX table, that is written to at the same time as the saga transaction meaning saga,outbox and order status is stored locally for each microservice
- a scheduler to pull events from the OUTBOX table with the use of a scheduler, the Pulling Outbox Table approach.

<i>An alternative is the Change Data Capture method that listens to transaction logs</i>
 ### So the OUTBOX pattern
- Means Saga and order status are kept track of in the new local Outbox table
- Ensures Idempotency
     > Idempotency in the context of databases refers to the property where performing an operation multiple times produces the same result as performing it once, ensuring consistency regardless of how many times the operation is executed.
- Prevents data corruption with Optimistic Locks and database contstraints:
   >    Optimistic locks involve allowing multiple transactions to proceed concurrently, but checking for conflicts during commit to ensure data consistency, while database constraints are predefined rules that restrict the type or range of data that can be inserted, updated, or deleted in a database table.


