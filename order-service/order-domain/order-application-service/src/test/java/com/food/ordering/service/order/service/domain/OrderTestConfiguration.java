package com.food.ordering.service.order.service.domain;

import com.food.ordering.system.order.service.domain.OrderDomainService;
import com.food.ordering.system.order.service.domain.OrderDomainServiceImpl;
import com.food.ordering.system.order.service.domain.ports.output.message.publisher.payment.OrderCancelledPaymentRequestMessagePublisher;
import com.food.ordering.system.order.service.domain.ports.output.message.publisher.payment.OrderCreatedPaymentRequestMessagePublisher;
import com.food.ordering.system.order.service.domain.ports.output.message.publisher.restaurantapproval.OrderPaidRestaurantRequestMessagePublisher;
import com.food.ordering.system.order.service.domain.ports.output.repository.CustomerRepository;
import com.food.ordering.system.order.service.domain.ports.output.repository.OrderRepository;
import com.food.ordering.system.order.service.domain.ports.output.repository.RestaurantRepository;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * Given there is no main class at present, everything will need to be injected into the spring context that
 * has been created here with:
 * @SpringBootApplication
 *
 * Mocks are returned from calls to required classes
 */
@SpringBootApplication(scanBasePackages = "com.food.ordering.system")
public class OrderTestConfiguration {

/**
 * Because the domain logic is purposely isolated from any frameworks a 'real' bean has to be provided here.
 * @return
 */
@Bean
public OrderDomainService orderDomainService() {
      return new OrderDomainServiceImpl();
}

@Bean
public OrderCreatedPaymentRequestMessagePublisher orderCreatedPaymentRequestMessagePublisher() {
      return Mockito.mock(OrderCreatedPaymentRequestMessagePublisher.class);
}

@Bean
public OrderCancelledPaymentRequestMessagePublisher orderCancelledPaymentRequestMessagePublisher() {
      return Mockito.mock(OrderCancelledPaymentRequestMessagePublisher.class);
}

@Bean
public OrderPaidRestaurantRequestMessagePublisher orderPaidRestaurantRequestMessagePublisher() {
      return Mockito.mock(OrderPaidRestaurantRequestMessagePublisher.class);
}

@Bean
public OrderRepository orderRepository() {
      return Mockito.mock(OrderRepository.class);
}

@Bean
public CustomerRepository customerRepository() {
      return Mockito.mock(CustomerRepository.class);
}

@Bean
public RestaurantRepository restaurantRepository() {
      return Mockito.mock(RestaurantRepository.class);
}


}
