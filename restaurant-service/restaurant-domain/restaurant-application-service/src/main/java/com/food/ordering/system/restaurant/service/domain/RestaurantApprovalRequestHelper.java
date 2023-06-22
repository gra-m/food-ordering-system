package com.food.ordering.system.restaurant.service.domain;

import com.food.ordering.system.domain.valueobject.OrderId;
import com.food.ordering.system.domain.valueobject.RestaurantId;
import com.food.ordering.system.restaurant.service.domain.dto.RestaurantApprovalRequest;
import com.food.ordering.system.restaurant.service.domain.entity.Restaurant;
import com.food.ordering.system.restaurant.service.domain.event.OrderApprovalEvent;
import com.food.ordering.system.restaurant.service.domain.exception.RestaurantNotFoundException;
import com.food.ordering.system.restaurant.service.domain.mapper.RestaurantDataMapper;
import com.food.ordering.system.restaurant.service.domain.ports.output.message.publiser.OrderApprovedMessagePublisher;
import com.food.ordering.system.restaurant.service.domain.ports.output.message.publiser.OrderRejectedMessagePublisher;
import com.food.ordering.system.restaurant.service.domain.ports.output.repository.OrderApprovalRepository;
import com.food.ordering.system.restaurant.service.domain.ports.output.repository.RestaurantRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
public class RestaurantApprovalRequestHelper {
private final RestaurantDomainService restaurantDomainService;
private final RestaurantDataMapper restaurantDataMapper;
private final RestaurantRepository restaurantRepository;
private final OrderApprovalRepository orderApprovalRepository;
private final OrderApprovedMessagePublisher orderApprovedMessagePublisher;
private final OrderRejectedMessagePublisher orderRejectedMessagePublisher;


public RestaurantApprovalRequestHelper(RestaurantDomainService restaurantDomainService,
                                       RestaurantDataMapper restaurantDataMapper,
                                       RestaurantRepository restaurantRepository,
                                       OrderApprovalRepository orderApprovalRepository,
                                       OrderApprovedMessagePublisher orderApprovedMessagePublisher,
                                       OrderRejectedMessagePublisher orderRejectedMessagePublisher) {
    this.restaurantDomainService = restaurantDomainService;
    this.restaurantDataMapper = restaurantDataMapper;
    this.restaurantRepository = restaurantRepository;
    this.orderApprovalRepository = orderApprovalRepository;
    this.orderApprovedMessagePublisher = orderApprovedMessagePublisher;
    this.orderRejectedMessagePublisher = orderRejectedMessagePublisher;
}

@Transactional
public OrderApprovalEvent persistOrderApproval(RestaurantApprovalRequest restaurantApprovalRequest) {
    log.info("Processing restaurant approval for order id: {}", restaurantApprovalRequest.getOrderId());
    List<String> failureMessages = Collections.emptyList();
    Restaurant restaurant = findRestaurant(restaurantApprovalRequest);

    OrderApprovalEvent orderApprovalEvent = restaurantDomainService.validateOrder(restaurant,
    failureMessages,
    orderApprovedMessagePublisher,
    orderRejectedMessagePublisher);

    orderApprovalRepository.save(restaurant.getOrderApproval());

    return orderApprovalEvent;
}

private Restaurant findRestaurant(RestaurantApprovalRequest restaurantApprovalRequest) {
    Restaurant restaurant = getRestaurantFromRestaurantApprovalRequest(restaurantApprovalRequest);
    RestaurantId restaurantId = restaurant.getId();

    Optional<Restaurant> restaurantResult = restaurantRepository.findRestaurantInformation(restaurant);

    Restaurant restaurantEntity = checkRestaurantExistsInDb(restaurantResult, restaurantId);

    return updateRestaurantDomainObjectFromRetrievedEntity(restaurant,
    restaurantEntity,
    UUID.fromString(restaurantApprovalRequest.getOrderId()));

}

private Restaurant updateRestaurantDomainObjectFromRetrievedEntity(Restaurant restaurant,
                                                                   Restaurant restaurantEntity,
                                                                   UUID orderIdFromApprovalRequest) {
    restaurant.setActive(restaurantEntity.isActive());
    restaurant.getOrderDetail().getProducts().forEach(product -> {
        restaurantEntity.getOrderDetail().getProducts().forEach(p -> {
            if( p.getId().equals(product.getId()) ) {
                product.updateWithConfirmedNamePriceAndAvailability(p.getName(), p.getPrice(), p.isAvailable());
            }
        });
    });

    restaurant.getOrderDetail().setId(new OrderId(orderIdFromApprovalRequest));
    return restaurant;

}

private Restaurant getRestaurantFromRestaurantApprovalRequest(RestaurantApprovalRequest restaurantApprovalRequest) {
    return restaurantDataMapper.restaurantApprovalRequestToRestaurant(restaurantApprovalRequest);
}

private Restaurant checkRestaurantExistsInDb(Optional<Restaurant> restaurantResult, RestaurantId restaurantId) {
    if( restaurantResult.isEmpty() ) {
        log.error("RestaurantApprovalRequestHelper says: Restaurant with id {} not found!", restaurantId.getValue());

        throw new RestaurantNotFoundException(String.format("Restaurant with id %s not found!",
        restaurantId.getValue()));
    } else return restaurantResult.get();
}


}
