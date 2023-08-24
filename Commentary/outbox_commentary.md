# Steps and info
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