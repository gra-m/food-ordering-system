package com.food.ordering.system.order.service.domain.ports.input.service;

import com.food.ordering.system.order.service.domain.dto.create.CreateOrderCommand;
import com.food.ordering.system.order.service.domain.dto.create.CreateOrderResponse;
import com.food.ordering.system.order.service.domain.dto.track.TrackOrderQuery;
import com.food.ordering.system.order.service.domain.dto.track.TrackOrderResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;


/**
 * Implementation of OrderApplicationService. Encapsulated with package private as both
 * impl and interface are in the same package. This class is simplified with the use of two
 * 'helper' @Components OrderCreateCommandHandler and     ...
 */
@Slf4j
@Validated
@Service
class OrderApplicationServiceImpl implements OrderApplicationService {

private final OrderCreateCommandHandler orderCreateCommandHandler;
private final OrderTrackCommandHandler orderTrackCommandHandler;

public OrderApplicationServiceImpl(OrderCreateCommandHandler orderCreateCommandHandler,
                                   OrderTrackCommandHandler orderTrackCommandHandler) {
    this.orderCreateCommandHandler = orderCreateCommandHandler;
    this.orderTrackCommandHandler = orderTrackCommandHandler;
}

/**
 * @param createOrderCommand
 * @return
 */
@Override
public CreateOrderResponse createOrder(CreateOrderCommand createOrderCommand) {
    return orderCreateCommandHandler.createOrder(createOrderCommand);
}

/**
 * @param trackOrderQuery
 * @return
 */
@Override
public TrackOrderResponse trackOrder(TrackOrderQuery trackOrderQuery) {
    return orderTrackCommandHandler.trackOrder(trackOrderQuery);
}


}
