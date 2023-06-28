package com.food.ordering.system.order.service.domain;

import com.food.ordering.system.order.service.domain.entity.Order;
import com.food.ordering.system.order.service.domain.exception.OrderNotFoundException;
import com.food.ordering.system.order.service.domain.ports.output.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
public class OrderSagaHelper {
private final OrderRepository orderRepository;

public OrderSagaHelper(OrderRepository orderRepository) {
    this.orderRepository = orderRepository;
}

Order findOrder(String orderId) {
    Optional<Order> orderResponse = orderRepository.findById(orderId);

    if( orderResponse.isEmpty() ) {
        log.error("Order with id: {} could not be found!", orderId);
        throw new OrderNotFoundException(String.format("order with id %s could not be found!", orderId));
    }
    return orderResponse.get();
}

void saveOrder(Order order) {
    orderRepository.save(order);
}


}