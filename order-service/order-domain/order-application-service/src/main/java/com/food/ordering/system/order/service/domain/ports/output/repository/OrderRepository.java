package com.food.ordering.system.order.service.domain.ports.output.repository;

import com.food.ordering.system.order.service.domain.entity.Order;
import com.food.ordering.system.order.service.domain.valueobject.TrackingId;

import java.util.Optional;

/**
 * Repository interfaces will be implemented in the data access layer via adapters
 */
public interface OrderRepository {
Order save(Order order);

Optional<Order> findByTrackingId(TrackingId trackingId);
}