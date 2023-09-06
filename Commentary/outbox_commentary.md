# Steps and info
## 81. Double pay test working but..
#### temp fix:
- TestingOrderPaymentSaga DoublePayment test runs BUT only with removal of NOTNULL on created_at and type in order.payment_outbox/restaurant_approval_outbox
## 70.




## 69b/c/d.: PaymentOutboxHelper

> 69b Added deleted message publishing classes back -> too much red-line noise..

<details>
<summary>Order Pending and Cancelling Outbox messages returned</summary>

>order-service/order-domain/order-application-service/src/main/java/com/food/ordering/system/order/service/domain/outbox/scheduler/payment/PaymentOutboxScheduler.java 
```java
public void processOutboxMessage() {
Optional<List<OrderPaymentOutboxMessage>> outboxMessageResponse =
paymentOutboxHelper.getPaymentOutboxMessageByOutboxStatusAndSagaStatus(
OutboxStatus.STARTED, SagaStatus.STARTED, SagaStatus.COMPENSATING);
}

```
</details>

<details>
<summary>
Scheduling @Transactional [Spring Managed Transaction]
</summary>

```java
// Explicitly state and make that only is:
@Transactional(readOnly = true)

@Override
@Transactional
@Scheduled(fixedDelayString = "${order-service.outbox-scheduler-fixed-rate}",
initialDelayString = "${order-service.outbox-scheduler-initial-delay}")
public void processOutboxMessage() {

}
```

> Note: ${order-service.outbox-scheduler-fixed-rate}" is refrencing the application.yml that is in the order
> container module within order-service, the method will run every 10 seconds.

```yaml
  server:
  port: 8181
# logging level set at base package com.food.ordering.system
logging:
  level:
    com.food.ordering.system: DEBUG
# names of the four Kafka topics 'order-service' and outbox-scheduler configs, set in OrderServiceConfigData
order-service:
  payment-request-topic-name: payment-request
  payment-response-topic-name: payment-response
  restaurant-approval-request-topic-name: restaurant-approval-request
  restaurant-approval-response-topic-name: restaurant-approval-response
  outbox-scheduler-fixed-rate: 10000
  outbox-scheduler-initial-delay: 10000
```
</details>

>   A message bus, within the context of the outbox pattern, is a communication channel that asynchronously routes and delivers events or messages from a service's internal outbox to external consumers, ensuring reliable and consistent data propagation.

   <details>
<summary>The safety of outbox</summary>

> The mechanism of Outbox in this java implementation can be summed up with the use of this Functional Interface:
```java
     public interface PaymentRequestMessagePublisher {

    void publish(OrderPaymentOutboxMessage orderPaymentOutboxMessage,
                 BiConsumer<OrderPaymentOutboxMessage, OutboxStatus> outboxCallback);
}
```
>  order-service/order-domain/order-application-service/src/main/java/com/food/ordering/system/order/service/domain/ports/output/message/publisher/payment/PaymentRequestMessagePublisher.java

> The BiConsumer is called with the actual outcome of the asynch call back, meaning that a local outbox table is
> only updated with actual confirmed data. The data transaction is called on to be saved from a context where the outcome is
> known to the messaging service.
> HERE! the method is sent to be called later, when a certainty is known

```java
        @Override
  @Transactional
  @Scheduled(
      fixedDelayString = "${order-service.outbox-scheduler-fixed-rate}",
      initialDelayString = "${order-service.outbox-scheduler-initial-delay}")
  public void processOutboxMessage() {
    Optional<List<OrderPaymentOutboxMessage>> outboxMessagesResponse =
        paymentOutboxHelper.getPaymentOutboxMessageByOutboxStatusAndSagaStatus(
            OutboxStatus.STARTED, SagaStatus.STARTED, SagaStatus.COMPENSATING);
    if (outboxMessagesResponse.isPresent() && outboxMessagesResponse.get().size() > 0) {
     List<OrderPaymentOutboxMessage> outboxMessages = outboxMessagesResponse.get();
     log.info("Received {} OrderPaymentOutboxMessage with ids {}, sending to message bus!",
     outboxMessages.size(),
     outboxMessages.stream().map(outboxMessage -> outboxMessage.getId().toString()).collect(Collectors.joining(",")));
     outboxMessages.forEach(outboxMessage -> 
     paymentRequestMessagePublisher.publish(outboxMessage,
     // HERE! (orderPaymentOutboxMessage, outboxStatus) -> updateOutboxStatus(orderPaymentOutboxMessage, outboxStatus)));
    }
  }
  
  private void updateOutboxStatus(OrderPaymentOutboxMessage orderPaymentOutboxMessage, OutboxStatus outboxStatus) {
    orderPaymentOutboxMessage.setOutboxStatus(outboxStatus);
    paymentOutboxHelper.save(orderPaymentOutboxMessage);
    log.info("OrderPaymentOutboxMessage is updated wit outbox status: {}", outboxStatus.name());
  }
```

> not seen it yet, but if the transaction fails when this method is called from the publish method implementation
> I guess we get a SAGA rollback
</details>





## 69a: Added scheduling to application

<details>

<summary>
enable scheduling with SchedulerConfig bean:
</summary>

```java
   /**
 * Configuration bean created that enables scheduling in application
 */
@Configuration
@EnableScheduling
public class SchedulerConfig {

```
</details>



## 68c.  40ee75469f3bafac6dd25f60880794bb9778cd9f

<details>
<summary>Saga to outbox statuses mapped in new SagaHelper method:</summary>

```javascript
      SagaStatus orderStatusToSagaStatus(OrderStatus orderStatus) {
    switch( orderStatus ) {
        case PAID ->  {return SagaStatus.PROCESSING;}
        case APPROVED -> {return SagaStatus.SUCCEEDED;}
        case CANCELLING -> {return SagaStatus.COMPENSATING;}
        case CANCELLED -> {return SagaStatus.COMPENSATED;}
        default -> {return SagaStatus.STARTED;} // PENDING
    }
}
```
</details>

## 68b.  447504d1c4bce92b10df885a370863d5e5d09585

> At this point code is broken as old saga only message publishers have been deleted.
> 
### Payment and Approval output ports added, old SAGA only ports deleted:
[new ports and old ports](https://imgur.com/wywotYN.png)

[new ports only](https://imgur.com/DjJ4fdj.png)
> "Output ports are nothing but interfaces to be implemented in the infrastructure models, the domain layer simply uses these interfaces, injecting them at runtime"

<details>
<summary>outboxCallback using Functional Interface BiConsumer</summary>

### Biconsumer accepts two generic type params and returns nothing
> It will be implemented in a method that returns void, this method definition will be passed to and called by the publish
> method this will enable update of outbox status as FAILED or COMPLETED based on the result of the publish operation.
> Only when status from Kafka producers asynch sent method is known will this be able to be set.


```java
public interface PaymentRequestMessagePublisher {

void publish(OrderPaymentOutboxMessage orderPaymentOutboxMessage, BiConsumer<OrderPaymentOutboxMessage,
OutboxStatus> outboxCallback);
}
```
</details>

## 68a. 8f8c64e9596c6b72da0d9f588b765c7605df3794 
[changed/new files](https://imgur.com/ondpYmZ.png)
## 67.      a6b4acd66dcfdc40cf5d47f4c7d1dcc13495e4c7
### Why two outbox tables are necessary for OrderService:
> There are two types of events PaymentService/OrderService and they must be segregated:
- OrderCancelledEvent and OrderCreatedEvent -> Publish to PaymentRequestTopic to trigger payment-service
- OrderPaidEvent -> Publish to RestaurantApprovalRequestTopic to trigger restaurant-service

<details>
<summary>order-container init-schema.sql additions</summary>

### init of new tables, indexes and enums

```postgresql
     DROP TYPE IF EXISTS saga_status;
CREATE TYPE saga_status AS ENUM ('STARTED', 'FAILED', 'SUCCEEDED', 'PROCESSING', 'COMPENSATING', 'COMPENSATED');

DROP TYPE IF EXISTS outbox_status;
CREATE TYPE outbox_status AS ENUM ('STARTED', 'COMPLETED', 'FAILED');

DROP TABLE IF EXISTS "order".payment_outbox CASCADE;

-- 
CREATE TABLE "order".payment_outbox
(
    id uuid NOT NULL,
    saga_id uuid NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    processed_at TIMESTAMP WITH TIME ZONE,
    -- String to hold the saga type eg. order-processing
    type character varying COLLATE pg_catalog."default" NOT NULL,
    -- event objects serialized to json for persisting in outbox table then deserialized on consumer side
    payload jsonb NOT NULL,
    outbox_status outbox_status NOT NULL,
    saga_status saga_status NOT NULL,
    order_status order_status NOT NULL,
    -- used for optimisti locking
    version integer NOT NULL,
    CONSTRAINT payment_outbox_pkey PRIMARY KEY (id)
);


CREATE INDEX "payment_outbox_saga_status"
    ON "order".payment_outbox
    (type, outbox_status, saga_status); -- querying outbox table using these fields so Indexed

CREATE UNIQUE INDEX "payment_outbox_saga_id"
    ON "order".payment_outbox
    (type, saga_id, saga_status); --saga of any type must only be in a single status at any given time.

DROP TABLE IF EXISTS "order".restaurant_approval_outbox CASCADE;

CREATE TABLE "order".restaurant_approval_outbox
(
    id uuid NOT NULL,
    saga_id uuid NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    processed_at TIMESTAMP WITH TIME ZONE,
    type character varying COLLATE pg_catalog."default" NOT NULL,
    payload jsonb NOT NULL,
    outbox_status outbox_status NOT NULL,
    saga_status saga_status NOT NULL,
    order_status order_status NOT NULL,
    version integer NOT NULL,
    CONSTRAINT restaurant_approval_outbox_pkey PRIMARY KEY (id)
);

CREATE INDEX "restaurant_approval_outbox_saga_status"
    ON "order".restaurant_approval_outbox
    (type, outbox_status, saga_status);

CREATE UNIQUE INDEX "restaurant_approval_outbox_saga_id"
    ON "order".restaurant_approval_outbox
    (type, saga_id, saga_status);

```
</details>

<details>
<summary>order-container application.yml for pulls from outbox:</summary>

> Ideally this should be no longer than 2000 dependent on the time required for transaction

```yaml
  outbox-scheduler-fixed-rate: 10000
  outbox-scheduler-initial-delay: 10000
```
</details>