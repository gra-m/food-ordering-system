package com.food.ordering.system.restaurant.service.domain.ports.output.message.publiser;

import com.food.ordering.system.domain.event.publisher.DomainEventPublisher;
import com.food.ordering.system.restaurant.service.domain.event.OrderApprovedEvent;

/**
 * An output port that is implemented in restaurant-service/restaurant-messaging publisher.kafka
 * .OrderApprovedKafkaMessagePublisher.
 * So, implementation to another messaging service possible there.
 */
public interface OrderApprovedMessagePublisher extends DomainEventPublisher<OrderApprovedEvent>
{


}
