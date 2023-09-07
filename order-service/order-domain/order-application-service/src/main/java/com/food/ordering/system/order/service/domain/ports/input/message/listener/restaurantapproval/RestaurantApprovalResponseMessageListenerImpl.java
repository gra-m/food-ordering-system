package com.food.ordering.system.order.service.domain.ports.input.message.listener.restaurantapproval;

import com.food.ordering.system.order.service.domain.OrderApprovalSaga;
import com.food.ordering.system.order.service.domain.dto.message.RestaurantApprovalResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import static com.food.ordering.system.order.service.domain.entity.Order.FAILURE_MESSAGE_DELIMITER;

/**
 * The order messaging module uses this interface after consuming restaurantApprovalResponseKafkaTopic.
 * <p>
 * Triggered by other domain events from other bound contexts BeanA [here] to BeanB[@Transactional] in
 * separate helper. SAGA complete once implemented
 */

@Slf4j
@Validated
@Service
public class RestaurantApprovalResponseMessageListenerImpl implements RestaurantApprovalResponseMessageListener
{
    private final OrderApprovalSaga orderApprovalSaga;

    public RestaurantApprovalResponseMessageListenerImpl(OrderApprovalSaga orderApprovalSaga)
    {
        this.orderApprovalSaga = orderApprovalSaga;
    }


    /**
     * @param restaurantApprovalResponse
     */
    @Override
    public void orderApproved(RestaurantApprovalResponse restaurantApprovalResponse)
    {
        orderApprovalSaga.process(restaurantApprovalResponse);
        log.info("Order is approved for order id: {}", restaurantApprovalResponse.getOrderId());
    }

    /**
     * @param restaurantApprovalResponse
     */
    @Override
    public void orderRejected(RestaurantApprovalResponse restaurantApprovalResponse)
    {
        orderApprovalSaga.rollback(restaurantApprovalResponse);
        log.info("Order Approval Saga rollback operation is completed for order id: {} with failure messages: {}",
                restaurantApprovalResponse.getOrderId(),
                String.join(FAILURE_MESSAGE_DELIMITER, restaurantApprovalResponse.getFailureMessages()));
    }


}
