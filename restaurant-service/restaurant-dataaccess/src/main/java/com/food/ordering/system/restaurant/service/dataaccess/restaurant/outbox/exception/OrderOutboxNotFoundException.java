package com.food.ordering.system.restaurant.service.dataaccess.restaurant.outbox.exception;

public class OrderOutboxNotFoundException extends RuntimeException
{
    public OrderOutboxNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public OrderOutboxNotFoundException(String message) {
        super(message);
    }

}
