package com.food.ordering.system.domain.valueobject;

/**
 * RestaurantApprovalService only triggered with a paid order and this status will actually represent the requested
 * order's status.
 *
 * OrderApprovalStatus.APPROVED/REJECTED is used in the response object
 */
public enum RestaurantOrderStatus {
    PAID
}
