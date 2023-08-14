package com.food.ordering.system.order.service.domain;

import com.food.ordering.system.domain.event.publisher.DomainEventPublisher;
import com.food.ordering.system.order.service.domain.entity.Order;
import com.food.ordering.system.order.service.domain.entity.Product;
import com.food.ordering.system.order.service.domain.entity.Restaurant;
import com.food.ordering.system.order.service.domain.event.OrderCancelledEvent;
import com.food.ordering.system.order.service.domain.event.OrderCreatedEvent;
import com.food.ordering.system.order.service.domain.event.OrderPaidEvent;
import com.food.ordering.system.order.service.domain.exception.OrderDomainException;
import lombok.extern.slf4j.Slf4j;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import static com.food.ordering.system.domain.DomainConstants.UTCBRU;

/**
 * Used in this example to isolate Entities from the order application
 */
@Slf4j
public class OrderDomainServiceImpl implements OrderDomainService {


/**
 * <p>Given that it is unknown as to whether the given Order and Restaurant aggregates are valid, they need to
 * be validated before an OrderCreatedEvent can be returned.
 * Uses Order and Restaurant aggregates to check against business requirements for a valid order.
 * </p>
 * here did not work full stop until refactored BaseEntity to use BaseId.
 *
 * @param order                                 the order that has been created by the client
 * @param restaurant                            the restaurant for which the order has been created
 * @param orderCreatedEventDomainEventPublisher
 * @return An OrderCreatedEvent for further processing
 */
@Override
public OrderCreatedEvent validateAndInitiateOrder(Order order, Restaurant restaurant,
                                                  DomainEventPublisher<OrderCreatedEvent> orderCreatedEventDomainEventPublisher) {
      validateRestaurant(restaurant);
      setOrderProductInformation(order, restaurant);
      order.validateOrder();
      order.initializeOrder();
      log.info("Order with id: {} is initiated", order.getId().getValue());
      return new OrderCreatedEvent(order, ZonedDateTime.now(ZoneId.of(UTCBRU)),
      orderCreatedEventDomainEventPublisher);
}

/**
 * Given that (Order) has been confirmed as paid for
 *
 * @param order                              the order that has been paid
 * @param orderPaidEventDomainEventPublisher
 * @return an OrderPaidEvent for further actioning.
 */
@Override
public OrderPaidEvent payOrder(Order order,
                               DomainEventPublisher<OrderPaidEvent> orderPaidEventDomainEventPublisher) {
      order.pay();
      log.info("Order no {} has been paid", order.getId().getValue());
      return new OrderPaidEvent(order, ZonedDateTime.now(ZoneId.of(UTCBRU)), orderPaidEventDomainEventPublisher);
}

/**
 * Last event in Order state system, after approval there is no need to fire an event.
 * <p>
 * Action -> client will get order data and track using internal only
 *
 * @param order The order that is to be approved.
 * @see com.food.ordering.system.order.service.domain.valueobject.TrackingId
 * <p>
 * Note: if there was a delivery process this could return an event to the delivery handling microservice, for
 * this though we will be using http requests and the process will end here.
 */
@Override
public void approveOrder(Order order) {
      order.approve();
      log.info("Order no {} has been approved", order.getId().getValue());
}

/**
 * <h3>SAGA PATTERN Compensating transaction</h3>
 * <p>Example of compensating transaction within SAGA pattern, if Restaurant Service cannot complete order Payment
 * Service
 * needs to know</p>
 * <p>
 * for order PAID -> CANCELLING
 * 1. must first be confirmed as PAID
 * 2. restaurant cannot fulfill order
 *
 * @param order                                   the order that is being rolled back from PAID to CANCELLING
 * @param failureMessages                         -> need failure messages from other services for logs and customer
 * @param orderCancelledEventDomainEventPublisher
 */
@Override
public OrderCancelledEvent cancelOrderPayment(Order order, List<String> failureMessages,
                                              DomainEventPublisher<OrderCancelledEvent> orderCancelledEventDomainEventPublisher) {
      order.initCancel(failureMessages);
      log.info("Order payment is cancelling for id: {}", order.getId().getValue());
      return new OrderCancelledEvent(order, ZonedDateTime.now(ZoneId.of(UTCBRU)),
      orderCancelledEventDomainEventPublisher);
}

/**
 * for order PENDING/CANCELLING -> CANCELLED
 * 1. must first be confirmed as either PENDING OR CANCELLED
 * 2. payment must have failed PENDING -> CANCELLED
 * 2a. order could not be fulfilled CANCELLING -> CANCELLED
 *
 * @param order           the order that is going to be cancelled
 * @param failureMessages the list of failure messages linked to this order
 */
@Override
public void cancelOrder(Order order, List<String> failureMessages) {
      order.cancel(failureMessages);
      log.info("Order with id: {} is cancelled", order.getId().getValue());
}

/**
 * Given that it is unknown whether the current restaurant is active.
 * Again the need for a specific cast RestaurantId extends BaseId && BaseEntity&lt;BaseId&gt; not required in eg. code
 *
 * @param restaurant that needs to be confirmed as active
 */
private void validateRestaurant(Restaurant restaurant) {
      if( !restaurant.isActive() ) {
            throw new OrderDomainException(String.format("Restaurant with id %s is currently not active!",
                restaurant.getId().getValue()));
      }

}

/**
 * Given that the orderItems may have been populated with out-of-date information it is necessary to go to the database
 * source maintained by the restaurant itself. There would need to be a mechanism for bringing these changes to the
 * attention of the customer.
 * <p>
 * Current = O(n2) time complexity consider refactoring
 *
 * @param order      the order created by a client
 * @param restaurant the restaurant linked to providing this order to (chosen by) the client
 */
private void setOrderProductInformation(Order order, Restaurant restaurant) {
      order.getItems().forEach(orderItem -> restaurant.getProducts().forEach(restaurantProduct -> {
            Product currentProduct = orderItem.getProduct();
            if( currentProduct.equals(restaurantProduct) ) {
                  currentProduct.updateWithConfirmedNameAndPrice(restaurantProduct.getName(),
                      restaurantProduct.getPrice());
            }
      }));

}

}
