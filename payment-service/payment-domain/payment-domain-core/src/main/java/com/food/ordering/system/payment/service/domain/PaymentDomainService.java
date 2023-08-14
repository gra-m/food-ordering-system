package com.food.ordering.system.payment.service.domain;

import com.food.ordering.system.domain.event.publisher.DomainEventPublisher;
import com.food.ordering.system.payment.service.domain.entity.CreditEntry;
import com.food.ordering.system.payment.service.domain.entity.CreditHistory;
import com.food.ordering.system.payment.service.domain.entity.Payment;
import com.food.ordering.system.payment.service.domain.event.PaymentCancelledEvent;
import com.food.ordering.system.payment.service.domain.event.PaymentCompletedEvent;
import com.food.ordering.system.payment.service.domain.event.PaymentEvent;
import com.food.ordering.system.payment.service.domain.event.PaymentFailedEvent;

import java.util.List;

public interface PaymentDomainService {

/**
 * @param payment                                   the payment to be validated and initiated
 * @param creditEntry                               the CreditEntry to be added if valid
 * @param creditHistories                           the CreditHistory to be updated if valid
 * @param failureMessages                           the failure messages for logging of failures
 * @param paymentCompletedEventDomainEventPublisher Enables event publishing to be 'self-fired' without adding extra
 *                                                  dependencies to domain-core
 * @param paymentFailedEventDomainEventPublisher
 * @return a PaymentEvent to be returned, payment events
 */
PaymentEvent validateAndInitiatePayment(Payment payment,
                                        CreditEntry creditEntry,
                                        List<CreditHistory> creditHistories,
                                        List<String> failureMessages,
                                        DomainEventPublisher<PaymentCompletedEvent> paymentCompletedEventDomainEventPublisher,
                                        DomainEventPublisher<PaymentFailedEvent> paymentFailedEventDomainEventPublisher);

PaymentEvent validateAndCancelPayment(Payment payment,
                                      CreditEntry creditEntry,
                                      List<CreditHistory> creditHistories,
                                      List<String> failureMessages,
                                      DomainEventPublisher<PaymentCancelledEvent> paymentCancelledEventDomainEventPublisher,
                                      DomainEventPublisher<PaymentFailedEvent> paymentFailedEventDomainEventPublisher);


}
