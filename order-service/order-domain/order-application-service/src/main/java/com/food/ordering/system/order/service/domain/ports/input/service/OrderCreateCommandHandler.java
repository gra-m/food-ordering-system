package com.food.ordering.system.order.service.domain.ports.input.service;

import com.food.ordering.system.order.service.domain.OrderDomainService;
import com.food.ordering.system.order.service.domain.dto.create.CreateOrderCommand;
import com.food.ordering.system.order.service.domain.dto.create.CreateOrderResponse;
import com.food.ordering.system.order.service.domain.entity.Customer;
import com.food.ordering.system.order.service.domain.entity.Order;
import com.food.ordering.system.order.service.domain.entity.Restaurant;
import com.food.ordering.system.order.service.domain.event.OrderCreatedEvent;
import com.food.ordering.system.order.service.domain.exception.OrderDomainException;
import com.food.ordering.system.order.service.domain.mapper.OrderDataMapper;
import com.food.ordering.system.order.service.domain.ports.output.repository.CustomerRepository;
import com.food.ordering.system.order.service.domain.ports.output.repository.OrderRepository;
import com.food.ordering.system.order.service.domain.ports.output.repository.RestaurantRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
public class OrderCreateCommandHandler {

private final OrderDomainService orderDomainService;
private final OrderRepository orderRepository;
private final CustomerRepository customerRepository;
private final RestaurantRepository restaurantRepository;
private final OrderDataMapper orderDataMapper;

public OrderCreateCommandHandler(OrderDomainService orderDomainService, OrderRepository orderRepository,
                                 CustomerRepository customerRepository, RestaurantRepository restaurantRepository,
                                 OrderDataMapper orderDataMapper) {
      this.orderDomainService = orderDomainService;
      this.orderRepository = orderRepository;
      this.customerRepository = customerRepository;
      this.restaurantRepository = restaurantRepository;
      this.orderDataMapper = orderDataMapper;
}


/**
 * @ Current commit 20 -> OrderCreatedEvent and saveOrder(order) are not currently atomic. This will be changed when
 * covering the SAGA pattern. Save first -> then publish orderCreatedEvent.
 * @param createOrderCommand
 * @return
 */
@Transactional
public CreateOrderResponse createOrder(CreateOrderCommand createOrderCommand) {
      checkCustomer(createOrderCommand.getCustomerId());
      Restaurant restaurant = checkRestaurant(createOrderCommand);
      Order order = orderDataMapper.createOrderCommandToOrder(createOrderCommand);
      OrderCreatedEvent orderCreatedEvent = orderDomainService.validateAndInitiateOrder(order, restaurant);
      Order orderResult = saveOrder(order);
      log.info("Order with id: {} created", orderResult.getId());

      return orderDataMapper.orderToCreateOrderResponse(orderResult);
}




/**
 * Given that a CreateOrderCommand has been received, it will be used to create a bare-bones [id and product
 * list with id] Restaurant. This is then used to create a fully up-to-date restaurant object using the
 * restaurantRepository.
 *
 * @param createOrderCommand the createOrderCommand...
 * @return
 */
private Restaurant checkRestaurant(CreateOrderCommand createOrderCommand) {
      Restaurant restaurant = orderDataMapper.createOrderCommandToRestaurant(createOrderCommand);
      Optional<Restaurant> restaurantOpt = restaurantRepository.findRestaurantInformation(restaurant);
      if( restaurantOpt.isEmpty() ) {
            UUID restId = createOrderCommand.getRestaurantId();
            log.warn("Could not find restaurant with restaurant id {}", restId);
            throw new OrderDomainException(String.format("Could not find restaurant with restaurant id %s", restId));
      }
      return restaurantOpt.get();
}

/**
 * Fail-Fast: Quickly checking availability of customer is dealt with here, if more checks were required
 * it would be best to send customer object to Domain-Service and carry out logic there.
 *
 * @param customerId UUID for a customer
 */
private void checkCustomer(UUID customerId) {
      Optional<Customer> customer = customerRepository.findCustomer(customerId);
      if( customer.isEmpty() ) {
            log.warn("Could not find customer with customer id {}", customerId);
            throw new OrderDomainException(String.format("Could not find customer with customer id %s", customer));
      }
}


/**
 *
 * @param order
 * @return
 */
private Order saveOrder(Order order) {
      Order savedOrder = orderRepository.save(order);
      if ( Objects.isNull(savedOrder)) {
            log.warn("Attempt to save order: {} failed", order);
            throw new OrderDomainException(String.format("Attempt to save order %s failed", order));
      }
      log.info("Order id {} save successful", order.getId());
      return savedOrder;
}

}
