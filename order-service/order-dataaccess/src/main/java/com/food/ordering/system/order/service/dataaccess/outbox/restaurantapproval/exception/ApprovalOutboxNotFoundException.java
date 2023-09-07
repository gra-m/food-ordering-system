package com.food.ordering.system.order.service.dataaccess.outbox.restaurantapproval.exception;

public class ApprovalOutboxNotFoundException extends RuntimeException
{

    public ApprovalOutboxNotFoundException(String message, Exception e)
    {
        super(message, e);
    }

    public ApprovalOutboxNotFoundException(String message)
    {
        super(message);
    }
}
