package com.food.ordering.system.order.service.dataaccess.order.repository;

import com.food.ordering.system.order.service.dataaccess.order.entity.OrderEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * This repository will be used internally to complete database operations
 * JpaRepository[T, ID] is extended all the way up to Repository[T, ID] via etc..
 * <p>
 * Behind the scenes with @Repository Spring creates a dedicated Repository proxy class that will handle the
 * method calls.
 */
@Repository
public interface OrderJpaRepository extends JpaRepository<OrderEntity, UUID> {
Optional<OrderEntity> findByTrackingId(UUID trackingId);


}
