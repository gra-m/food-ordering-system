package com.food.ordering.system.payment.service.domain;

import com.food.ordering.system.domain.valueobject.CustomerId;
import com.food.ordering.system.payment.service.domain.dto.PaymentRequest;
import com.food.ordering.system.payment.service.domain.entity.CreditEntry;
import com.food.ordering.system.payment.service.domain.entity.CreditHistory;
import com.food.ordering.system.payment.service.domain.entity.Payment;
import com.food.ordering.system.payment.service.domain.event.PaymentEvent;
import com.food.ordering.system.payment.service.domain.exception.PaymentApplicationServiceException;
import com.food.ordering.system.payment.service.domain.mapper.PaymentDataMapper;
import com.food.ordering.system.payment.service.domain.ports.output.message.publisher.PaymentCancelledMessagePublisher;
import com.food.ordering.system.payment.service.domain.ports.output.message.publisher.PaymentCompletedMessagePublisher;
import com.food.ordering.system.payment.service.domain.ports.output.message.publisher.PaymentFailedMessagePublisher;
import com.food.ordering.system.payment.service.domain.ports.output.repository.CreditEntryRepository;
import com.food.ordering.system.payment.service.domain.ports.output.repository.CreditHistoryRepository;
import com.food.ordering.system.payment.service.domain.ports.output.repository.PaymentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
/**
 * Given that database events have been persisted here with the help of this helper class -> fire domain events.
 *
 * @Component = a spring bean vs @Service = Spring managed.
 */
@Slf4j
@Component
public class PaymentRequestHelper {

private final PaymentDomainService paymentDomainService;
private final PaymentDataMapper paymentDataMapper;
private final PaymentRepository paymentRepository;
private final CreditEntryRepository creditEntryRepository;
private final CreditHistoryRepository creditHistoryRepository;
private final PaymentCompletedMessagePublisher paymentCompletedEventDomainEventPublisher;
private final PaymentCancelledMessagePublisher paymentCancelledEventDomainEventPublisher;
private final PaymentFailedMessagePublisher paymentFailedEventDomainEventPublisher;

public PaymentRequestHelper(PaymentDomainService paymentDomainService,
                            PaymentDataMapper paymentDataMapper,
                            PaymentRepository paymentRepository,
                            CreditEntryRepository creditEntryRepository,
                            CreditHistoryRepository creditHistoryRepository,
                            PaymentCompletedMessagePublisher paymentCompletedEventDomainEventPublisher,
                            PaymentCancelledMessagePublisher paymentCancelledEventDomainEventPublisher,
                            PaymentFailedMessagePublisher paymentFailedEventDomainEventPublisher) {
    this.paymentDomainService = paymentDomainService;
    this.paymentDataMapper = paymentDataMapper;
    this.paymentRepository = paymentRepository;
    this.creditEntryRepository = creditEntryRepository;
    this.creditHistoryRepository = creditHistoryRepository;
    this.paymentCompletedEventDomainEventPublisher = paymentCompletedEventDomainEventPublisher;
    this.paymentCancelledEventDomainEventPublisher = paymentCancelledEventDomainEventPublisher;
    this.paymentFailedEventDomainEventPublisher = paymentFailedEventDomainEventPublisher;
}

/**
 * Given that a payment for the passed order id can be found, retrieve it  and pass it to:
 *
 * @param paymentRequest
 * @return
 * @See PaymentDomainService validateAndCancelPaymentj)
 * <p>
 * and save the payment (cancellation) in the state in which it is returned.
 */
@Transactional
public PaymentEvent persistCancelPayment(PaymentRequest paymentRequest) {
    log.info("Received payment rollback event for order id: {}", paymentRequest.getOrderId());

    Optional<Payment> paymentResponse = paymentRepository.findByOrderId(UUID.fromString(paymentRequest.getOrderId()));

    if( paymentResponse.isEmpty() ) {
        log.error("Payment with order id: {} could not be found and so could not be cancelled!",
        paymentRequest.getOrderId());

        throw new PaymentApplicationServiceException(String.format("Payment with order id: %s could not found and so "
        + "could not be cancelled!",
        paymentRequest.getOrderId()));
    }

    Payment payment = paymentResponse.get();
    CreditEntry creditEntry = getCreditEntry(payment.getCustomerId());
    List<CreditHistory> creditHistories = getCreditHistory(payment.getCustomerId());
    List<String> failureMessages = new ArrayList<>();
    PaymentEvent paymentEvent = paymentDomainService.validateAndCancelPayment(payment,
    creditEntry,
    creditHistories,
    failureMessages,
    paymentCancelledEventDomainEventPublisher,
    paymentFailedEventDomainEventPublisher);
    persistDbObjects(payment, creditEntry, creditHistories, failureMessages);

    return paymentEvent;
}

/**
 * Given a PaymentRequest is received,
 * Of note: payment is saved whatever the outcome, because if the payment failed its type will be FAILED and a record
 * of this failure is still required.
 * <p>
 * CreditHistory and CreditEntry are saved only if failureMessages is returned empty from:
 *
 * @param paymentRequest An externam payment request has been received
 * @return PaymentEvent after the payment has been saved
 * @See PaymentDomainServiceImpl validateAndInitiatePayment
 */
@Transactional
public PaymentEvent persistPayment(PaymentRequest paymentRequest) {
    log.info("Received payment complete event for order id: {}", paymentRequest.getOrderId());

    Payment payment = paymentDataMapper.paymentRequestModelToPayment(paymentRequest);
    CreditEntry creditEntry = getCreditEntry(payment.getCustomerId());
    List<CreditHistory> creditHistories = getCreditHistory(payment.getCustomerId());
    List<String> failureMessages = new ArrayList<>();
    PaymentEvent paymentEvent = paymentDomainService.validateAndInitiatePayment(payment,
    creditEntry,
    creditHistories,
    failureMessages,
    paymentCompletedEventDomainEventPublisher,
    paymentFailedEventDomainEventPublisher);
    persistDbObjects(payment, creditEntry, creditHistories, failureMessages);

    return paymentEvent;

}

private void persistDbObjects(Payment payment,
                              CreditEntry creditEntry,
                              List<CreditHistory> creditHistories,
                              List<String> failureMessages) {
    paymentRepository.save(payment);

    if( failureMessages.isEmpty() ) {
        creditEntryRepository.save(creditEntry);
        creditHistoryRepository.save(creditHistories.get(creditHistories.size() - 1));
    }
}

private List<CreditHistory> getCreditHistory(CustomerId customerId) {
    Optional<List<CreditHistory>> creditHistories = creditHistoryRepository.findByCustomerId(customerId);

    if( creditHistories.isEmpty() ) {
        log.error("Could not find credit history for customer: {}", customerId.getValue());

        throw new PaymentApplicationServiceException(String.format("Could not find credit history for customer: %s",
        customerId.getValue().toString()));
    }

    return creditHistories.get();
}

private CreditEntry getCreditEntry(CustomerId customerId) {
    Optional<CreditEntry> creditEntry = creditEntryRepository.findByCustomerId(customerId);

    if( creditEntry.isEmpty() ) {
        log.error("Could not find credit entry entry for customer: {}", customerId.getValue());

        throw new PaymentApplicationServiceException(String.format("Could not find credit entry for customer: %s",
        customerId.getValue().toString()));
    }

    return creditEntry.get();
}


}
