package com.food.ordering.system.order.service.application.rest;


import com.food.ordering.system.order.service.domain.dto.create.CreateOrderCommand;
import com.food.ordering.system.order.service.domain.dto.create.CreateOrderResponse;
import com.food.ordering.system.order.service.domain.dto.track.TrackOrderQuery;
import com.food.ordering.system.order.service.domain.dto.track.TrackOrderResponse;
import com.food.ordering.system.order.service.domain.ports.input.service.OrderApplicationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * In order to communicate with the domain layer: OrderController -> DomainService interface(order-applicatin-service)
 * -> Domain entities and domain related business code.
 *
 * Why applicationi/vnd.api.v1+json?  and not application/json. vnd.api uses JSONAPI protocol which is a framework
 * for building APIs that allows the client to fetch and modify interrelated entities...
 * <a href="https://stackoverflow.com/questions/28055526/header-value-application-vnd-apijson">StackOverflow...</a>
 *
 */

@Slf4j
@RestController
@RequestMapping(value = "/orders", produces = "application/vnd.api.v1+json")
public class OrderController {


private final OrderApplicationService orderApplicationService;

/**
 * OrderApplicationServiceImpl from order-domain module made available via constructor dependency injection of its interface
 *
 * @param orderApplicationService  @Service implementation
 */
public OrderController(OrderApplicationService orderApplicationService){
      this.orderApplicationService = orderApplicationService;
}


/**
 * On successfully creating an order return a Spring ResponseEntity  [200] along with createOrderResponse that is created
 * by OrderDataMapper only after the transaction has been confirmed as successful.
 *
 * <a href="https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/bind/annotation/RequestBody.html">Spring @RequestBody</a>
 * 
 * @param createOrderCommand
 * @return  Happy Path: Spring ResponseEntity.ok == [200] along with createOrderResponse
 */
@PostMapping
public ResponseEntity<CreateOrderResponse> createOrder(@RequestBody CreateOrderCommand createOrderCommand) {
      log.info("Creating order for customer: {} at restaurant: {}", createOrderCommand.getCustomerId(),
          createOrderCommand.getRestaurantId());
      CreateOrderResponse createOrderResponse = orderApplicationService.createOrder(createOrderCommand);
      log.info("Order created with Tracking id: {}", createOrderResponse.getOrderTrackingId());
      return ResponseEntity.ok(createOrderResponse);
}

/**
 * Happy Path: Given a trackingId for an existing order a @Transactional(readOnly=true) returns an order that has its details mapped
 * into a TrackOrderResponse by OrderDataMapper
 *
 * @param trackingId UUID of an order
 * @return   Happy Path: Spring ResponseEntity  [200] along with a TrackOrderResponse
 */
@GetMapping("/{trackingId}")
public ResponseEntity<TrackOrderResponse> getOrderByTrackingId(@PathVariable UUID trackingId) {
      TrackOrderResponse trackOrderResponse = orderApplicationService
          .trackOrder(TrackOrderQuery.builder().orderTrackingId(trackingId).build());
      log.info("Returning order status with tracking id: {}", trackOrderResponse.getOrderTrackingId());
      return ResponseEntity.ok(trackOrderResponse);
}


}
