package com.food.ordering.system.order.service.domain.ports.input.service;

import com.food.ordering.system.domain.event.DomainEvent;
import com.food.ordering.system.domain.event.publisher.DomainEventPublisher;
import com.food.ordering.system.order.service.domain.event.OrderEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class ApplicatioinDomainEventPublisher implements ApplicationEventPublisherAware, DomainEventPublisher {

private ApplicationEventPublisher applicationEventPublisher;

/**
 * @param applicationEventPublisher
 */
@Override
public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
      this.applicationEventPublisher = applicationEventPublisher;

}

/**
 * @param domainEvent
 */
@Override
public void publish(DomainEvent domainEvent) {
      this.applicationEventPublisher.publishEvent(domainEvent);
      log.info("OrderCreatedEvent is published for order id: {}",
          ((OrderEvent)domainEvent).getOrder().getId().getValue());
}

}
