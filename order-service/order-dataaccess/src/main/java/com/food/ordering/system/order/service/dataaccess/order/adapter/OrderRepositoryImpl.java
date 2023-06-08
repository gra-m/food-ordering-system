package com.food.ordering.system.order.service.dataaccess.order.adapter;

import com.food.ordering.system.order.service.dataaccess.order.mapper.OrderDataAccessMapper;
import com.food.ordering.system.order.service.dataaccess.order.repository.OrderJpaRepository;
import com.food.ordering.system.order.service.domain.entity.Order;
import com.food.ordering.system.order.service.domain.ports.output.repository.OrderRepository;
import com.food.ordering.system.order.service.domain.valueobject.TrackingId;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * todo explain
 * Implements output port OrderRepository
 * Spring managed through use of @Component, not a Repository but an adapter class:
 */
@Component
public class OrderRepositoryImpl implements OrderRepository {

private final OrderDataAccessMapper orderDataAccessMapper;
private final OrderJpaRepository orderJpaRepository;

public OrderRepositoryImpl(OrderDataAccessMapper orderDataAccessMapper, OrderJpaRepository orderJpaRepository) {
      this.orderDataAccessMapper = orderDataAccessMapper;
      this.orderJpaRepository = orderJpaRepository;
}

/**
 * @param order Order
 * @return order Order
 */
@Override
public Order save(Order order) {
      return orderDataAccessMapper.orderEntityToOrder(
          orderJpaRepository.save(orderDataAccessMapper.orderToOrderEntity(order)));
}

/**
 * Interesting mapping method is carried out on optional, so, must be resolved automatically within map to pass to?
 *
 * @param trackingId the trackingId for the order
 * @return an optional Order domain object
 */
@Override
public Optional<Order> findByTrackingId(TrackingId trackingId) {
      /* I changed back to optional --->
      return Optional.of(orderDataAccessMapper.orderEntityToOrder(orderJpaRepository.findByTrackingId(trackingId.getValue()).get()));
      return orderJpaRepository.findByTrackingId(trackingId.getValue()).map(optionalOrderEntity ->
      orderDataAccessMapper.orderEntityToOrder(optionalOrderEntity) );*/
      return orderJpaRepository.findByTrackingId(trackingId.getValue()).map(orderDataAccessMapper::orderEntityToOrder);

}

}
