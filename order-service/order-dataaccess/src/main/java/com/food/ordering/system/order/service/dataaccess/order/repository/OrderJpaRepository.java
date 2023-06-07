package com.food.ordering.system.order.service.dataaccess.order.repository;

import com.food.ordering.system.order.service.dataaccess.order.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 *   JpaRepository[T, ID] is extended all the way up to Repository[T, ID] via etc..
 *
 *   Behind the scenes with @Repository Spring creates a dedicated Repository proxy class that will handle the
 *   method calls.
 */
@Repository
public interface OrderJpaRepository extends JpaRepository<OrderEntity, UUID> {
      Optional<OrderEntity> findByTrackingId(UUID trackingId);
}
