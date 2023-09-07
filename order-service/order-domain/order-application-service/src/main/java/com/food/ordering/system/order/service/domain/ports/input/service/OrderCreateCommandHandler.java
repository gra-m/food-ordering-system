package com.food.ordering.system.order.service.domain.ports.input.service;

import com.food.ordering.system.order.service.domain.OrderSagaHelper;
import com.food.ordering.system.order.service.domain.dto.create.CreateOrderCommand;
import com.food.ordering.system.order.service.domain.dto.create.CreateOrderResponse;
import com.food.ordering.system.order.service.domain.event.OrderCreatedEvent;
import com.food.ordering.system.order.service.domain.mapper.OrderDataMapper;
import com.food.ordering.system.order.service.domain.outbox.scheduler.payment.PaymentOutboxHelper;
import com.food.ordering.system.outbox.OutboxStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Component
public class OrderCreateCommandHandler
{
    private final OrderCreateHelper orderCreateHelper;
    private final OrderDataMapper orderDataMapper;
    private final PaymentOutboxHelper paymentOutboxHelper;
    private final OrderSagaHelper orderSagaHelper;

    public OrderCreateCommandHandler(OrderCreateHelper orderCreateHelper,
                                     OrderDataMapper orderDataMapper,
                                     PaymentOutboxHelper paymentOutboxHelper,
                                     OrderSagaHelper orderSagaHelper)
    {
        this.orderCreateHelper = orderCreateHelper;
        this.orderDataMapper = orderDataMapper;
        this.paymentOutboxHelper = paymentOutboxHelper;
        this.orderSagaHelper = orderSagaHelper;
    }

    /**
     * @param createOrderCommand
     * @return
     * @ Current commit 21 refactor branch persistOrder logic moved to OrderCreateHelper, once persisted the
     * orderCreatedEvent is published in the orderCreatedPaymentRequestMessagePublisher, all messaging is
     * separated from domain logic.
     */
    @Transactional
    public CreateOrderResponse createOrder(CreateOrderCommand createOrderCommand)
    {
        OrderCreatedEvent orderCreatedEvent = orderCreateHelper.persistOrder(createOrderCommand);
        log.info("Order with id {} was created", orderCreatedEvent.getOrder().getId().getValue());
        CreateOrderResponse createOrderResponse =
                orderDataMapper.orderToCreateOrderResponse(orderCreatedEvent.getOrder(), "order created successfully");

        paymentOutboxHelper.savePaymentOutboxMessage(orderDataMapper.orderCreatedEventToOrderPaymentEventPayload(orderCreatedEvent),
                orderCreatedEvent.getOrder().getOrderStatus(),
                orderSagaHelper.orderStatusToSagaStatus(orderCreatedEvent.getOrder().getOrderStatus()),
                OutboxStatus.STARTED,
                UUID.randomUUID()
        );

        log.info("Returning CreateOrderResponse with orderid: {}", orderCreatedEvent.getOrder().getId());

        return createOrderResponse;
    }


}
