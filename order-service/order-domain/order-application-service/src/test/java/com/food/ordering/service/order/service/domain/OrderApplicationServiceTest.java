package com.food.ordering.service.order.service.domain;

import com.food.ordering.system.domain.valueobject.*;
import com.food.ordering.system.order.service.domain.dto.create.CreateOrderCommand;
import com.food.ordering.system.order.service.domain.dto.create.CreateOrderResponse;
import com.food.ordering.system.order.service.domain.dto.create.OrderAddress;
import com.food.ordering.system.order.service.domain.dto.create.OrderItem;
import com.food.ordering.system.order.service.domain.entity.Customer;
import com.food.ordering.system.order.service.domain.entity.Order;
import com.food.ordering.system.order.service.domain.entity.Product;
import com.food.ordering.system.order.service.domain.entity.Restaurant;
import com.food.ordering.system.order.service.domain.exception.OrderDomainException;
import com.food.ordering.system.order.service.domain.mapper.OrderDataMapper;
import com.food.ordering.system.order.service.domain.ports.input.service.OrderApplicationService;
import com.food.ordering.system.order.service.domain.ports.output.repository.CustomerRepository;
import com.food.ordering.system.order.service.domain.ports.output.repository.OrderRepository;
import com.food.ordering.system.order.service.domain.ports.output.repository.RestaurantRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Access to the mocks and SpringContext required via classes = OrderTestConfiguration.class
 *
 * @TestInstance.Lifecycle.PER_CLASS = allows new instance of class for each test method without needing to use static
 * methods and fields (default).
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes = OrderTestConfiguration.class)
public class OrderApplicationServiceTest {

private final UUID CUSTOMER_ID = UUID.fromString("d215b5f8-0249-4dc5-89a3-51fd148cfb41");
private final UUID RESTAURANT_ID = UUID.fromString("d215b5f8-0249-4dc5-89a3-51fd148cfb45");
private final UUID PRODUCT_ID = UUID.fromString("d215b5f8-0249-4dc5-89a3-51fd148cfb48");
private final UUID ORDER_ID = UUID.fromString("15a497c1-0f4b-4eff-b9f4-c402c8c07afb");
private final BigDecimal PRICE = new BigDecimal("200.00");

//region AutowiredActualSpringComponents
@Autowired
private OrderApplicationService orderApplicationService;
@Autowired
private OrderDataMapper orderDataMapper;
//endregion

//region AutowiredMockBeansFromOrderTestConfiguration
@Autowired
private OrderRepository orderRepository;
@Autowired
private CustomerRepository customerRepository;
@Autowired
private RestaurantRepository restaurantRepository;
//endregion

private CreateOrderCommand createOrderCommand;
private CreateOrderCommand createOrderCommandWrongPrice;
private CreateOrderCommand getCreateOrderCommandWrongProductPrice;
private Restaurant restaurantResponse;

@BeforeAll
public void init() {
    createOrderCommand = CreateOrderCommand
    .builder()
    .customerId(CUSTOMER_ID)
    .restaurantId(RESTAURANT_ID)
    .address(OrderAddress.builder().street("street_1").postalCode("1000CB").city("Paris").build())
    .price(PRICE)
    .items(List.of(OrderItem
    .builder()
    .productId(PRODUCT_ID)
    .quantity(1)
    .price(new BigDecimal("50.00"))
    .subTotal(new BigDecimal("50.00"))
    .build(),
    OrderItem
    .builder()
    .productId(PRODUCT_ID)
    .quantity(3)
    .price(new BigDecimal("50.00"))
    .subTotal(new BigDecimal("150.00"))
    .build()))
    .build();

    createOrderCommandWrongPrice = CreateOrderCommand
    .builder()
    .customerId(CUSTOMER_ID)
    .restaurantId(RESTAURANT_ID)
    .address(OrderAddress.builder().street("street_1").postalCode("1000CB").city("Paris").build())
    .price(new BigDecimal("250.00"))
    .items(List.of(OrderItem
    .builder()
    .productId(PRODUCT_ID)
    .quantity(1)
    .price(new BigDecimal("50.00"))
    .subTotal(new BigDecimal("50.00"))
    .build(),
    OrderItem
    .builder()
    .productId(PRODUCT_ID)
    .quantity(3)
    .price(new BigDecimal("50.00"))
    .subTotal(new BigDecimal("150.00"))
    .build()))
    .build();

    getCreateOrderCommandWrongProductPrice = CreateOrderCommand
    .builder()
    .customerId(CUSTOMER_ID)
    .restaurantId(RESTAURANT_ID)
    .address(OrderAddress.builder().street("street_1").postalCode("1000CB").city("Paris").build())
    .price(new BigDecimal("210.00"))
    .items(List.of(OrderItem
    .builder()
    .productId(PRODUCT_ID)
    .quantity(1)
    .price(new BigDecimal("60.00"))
    .subTotal(new BigDecimal("60.00"))
    .build(),
    OrderItem
    .builder()
    .productId(PRODUCT_ID)
    .quantity(3)
    .price(new BigDecimal("50.00"))
    .subTotal(new BigDecimal("150.00"))
    .build()))
    .build();

    Customer customer = new Customer();
    customer.setId(new CustomerId(CUSTOMER_ID));

    restaurantResponse = Restaurant
    .builder()
    .restaurantId(new RestaurantId(createOrderCommand.getRestaurantId()))
    .products(List.of(new Product(new ProductId(PRODUCT_ID), "product-1", new Money(new BigDecimal("50.00"))),
    new Product(new ProductId(PRODUCT_ID), "product-2", new Money(new BigDecimal("50" + ".00")))))
    .active(true)
    .build();


    Order order = orderDataMapper.createOrderCommandToOrder(createOrderCommand);
    order.setId(new OrderId(ORDER_ID));
    when(customerRepository.findCustomer(CUSTOMER_ID)).thenReturn(Optional.of(customer));
    when(restaurantRepository.findRestaurantInformation(orderDataMapper.createOrderCommandToRestaurant(
    createOrderCommand))).thenReturn(Optional.of(restaurantResponse));
    when(orderRepository.save(any(Order.class))).thenReturn(order);
}

/**
 * Path -> 1. orderApplicationService.createOrder(OrderCommandLikeIncomingJSON) 2.
 */
@Test
public void testCreateOrder() {
    CreateOrderResponse createOrderResponse = orderApplicationService.createOrder(createOrderCommand);
    assertEquals(OrderStatus.PENDING, createOrderResponse.getOrderStatus());
    assertEquals("Order created successfully", createOrderResponse.getMessage());
    assertNotNull(createOrderResponse.getOrderTrackingId());
}

@Test
public void testCreateOrderWithWrongTotalPrice() {
    OrderDomainException orderDomainException = assertThrows(OrderDomainException.class,
    () -> orderApplicationService.createOrder(createOrderCommandWrongPrice));
    assertEquals("Total price: 250.00 is not equal to Order Items Total: 200.00!", orderDomainException.getMessage());
}

@Test
public void testCreateOrderWithWrongProductPrice() {

    OrderDomainException orderDomainException = assertThrows(OrderDomainException.class,
    () -> orderApplicationService.createOrder(getCreateOrderCommandWrongProductPrice));
    assertEquals("Order item price: 60.00 is not valid for product: " + PRODUCT_ID, orderDomainException.getMessage());
}

@Test
public void testCreatedOrderWithPassiveRestaurant() {
    //given
    restaurantResponse = Restaurant
    .builder()
    .restaurantId(new RestaurantId(createOrderCommand.getRestaurantId()))
    .products(List.of(new Product(new ProductId(PRODUCT_ID), "product-1", new Money(new BigDecimal("50.00"))),
    new Product(new ProductId(PRODUCT_ID), "product-2", new Money(new BigDecimal("50" + ".00")))))
    .active(false)
    .build();

    when(restaurantRepository.findRestaurantInformation(orderDataMapper.createOrderCommandToRestaurant(
    createOrderCommand))).thenReturn(Optional.of(restaurantResponse));

    //then
    OrderDomainException orderDomainException = assertThrows(OrderDomainException.class,
    () -> orderApplicationService.createOrder(createOrderCommand));

    assertEquals(String.format("Restaurant with id %s is currently not active!", RESTAURANT_ID),
    orderDomainException.getMessage());
}


}

