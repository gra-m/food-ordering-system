DROP SCHEMA if EXISTS customer CASCADE;

CREATE SCHEMA customer;

CREATE EXTENSION if NOT EXISTS "uuid-ossp";

CREATE TABLE customer.customers
(
    id         uuid                                           NOT NULL,
    username   character varying COLLATE pg_catalog."default" NOT NULL,
    first_name character varying COLLATE pg_catalog."default" NOT NULL,
    last_name  character varying COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT customers_pkey PRIMARY KEY (id)
);

/*A materialized view is a physical representation of a query that may include one or two tables with a WHERE condition
  it is different to an ordinary view in that data is physically stored on disk and is in need of constant refreshing*/
DROP MATERIALIZED VIEW IF EXISTS customer.order_customer_m_view;

/*this view name was set in CustomerEntity with @Table(name = "order_customer_m_view", schema = "customer")*/
CREATE MATERIALIZED VIEW customer.order_customer_m_view
    TABLESPACE pg_default
AS
SELECT id,
       username,
       first_name,
       last_name
FROM customer.customers
WITH DATA;

refresh materialized VIEW customer.order_customer_m_view;

/*The following function is called on every data manipulation object on the customer table to keep order_customer_m_view
  up to date. The trigger that handles this is shown below: TRIGGER*/

DROP function if EXISTS customer.refresh_order_customer_m_view;

CREATE OR REPLACE function customer.refresh_order_customer_m_view()
    returns trigger
AS
'
    BEGIN
        REFRESH MATERIALIZED VIEW customer.order_customer_m_view;
        return null;
    END;
' LANGUAGE plpgsql;

/*TRIGGER*/

DROP trigger  if EXISTS refresh_order_customer_m_view ON customer.customers;

CREATE trigger refresh_order_customer_m_view
    after INSERT OR UPDATE OR DELETE OR truncate
    ON customer.customers FOR each statement
    EXECUTE PROCEDURE customer.refresh_order_customer_m_view();
