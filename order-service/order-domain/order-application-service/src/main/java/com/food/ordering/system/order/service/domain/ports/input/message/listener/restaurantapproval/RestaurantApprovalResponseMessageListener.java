package com.food.ordering.system.order.service.domain.ports.input.message.listener.restaurantapproval;

import com.food.ordering.system.order.service.domain.dto.message.RestaurantApprovalResponse;

/**
 * Ports?: These are nothing but interfaces that need to be implemented with adapters; there are two types of ports in
 * hexagonal architecture input and output.
 */
public interface RestaurantApprovalResponseMessageListener
{
    void orderApproved(RestaurantApprovalResponse restaurantApprovalResponse);

    void orderRejected(RestaurantApprovalResponse restaurantApprovalResponse);


}
