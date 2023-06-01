package com.food.ordering.system.order.service.domain.ports.input.message.listener.restaurantapproval;

import com.food.ordering.system.order.service.domain.dto.message.RestaurantApprovalResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

/**
 * Triggered by other domain events from other bound contexts BeanA [here] to BeanB[@Transactional] in
 * separate helper. SAGA complete once implemented
 */

@Slf4j
@Validated
@Service
public class RestaurantApprovalResponseMessageListenerImpl implements RestaurantApprovalResponseMessageListener{

/**
 * @param restaurantApprovalResponse
 */
@Override
public void orderApproved(RestaurantApprovalResponse restaurantApprovalResponse) {

}

/**
 * @param restaurantApprovalResponse
 */
@Override
public void orderRejected(RestaurantApprovalResponse restaurantApprovalResponse) {

}
}
