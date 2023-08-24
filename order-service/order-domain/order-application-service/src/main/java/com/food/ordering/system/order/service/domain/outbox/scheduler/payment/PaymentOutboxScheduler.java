package com.food.ordering.system.order.service.domain.outbox.scheduler.payment;

import com.food.ordering.system.outbox.OutboxScheduler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
@Slf4j
@Component
public class PaymentOutboxScheduler implements OutboxScheduler {


/**
*
*/
@Override
public void processOutboxMessage() {

}


}
