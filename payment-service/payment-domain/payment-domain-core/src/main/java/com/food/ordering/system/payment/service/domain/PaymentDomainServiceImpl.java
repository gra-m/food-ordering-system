package com.food.ordering.system.payment.service.domain;

import com.food.ordering.system.domain.event.publisher.DomainEventPublisher;
import com.food.ordering.system.domain.valueobject.Money;
import com.food.ordering.system.domain.valueobject.PaymentStatus;
import com.food.ordering.system.payment.service.domain.entity.CreditEntry;
import com.food.ordering.system.payment.service.domain.entity.CreditHistory;
import com.food.ordering.system.payment.service.domain.entity.Payment;
import com.food.ordering.system.payment.service.domain.event.PaymentCancelledEvent;
import com.food.ordering.system.payment.service.domain.event.PaymentCompletedEvent;
import com.food.ordering.system.payment.service.domain.event.PaymentEvent;
import com.food.ordering.system.payment.service.domain.event.PaymentFailedEvent;
import com.food.ordering.system.payment.service.domain.valueobject.CreditHistoryId;
import com.food.ordering.system.payment.service.domain.valueobject.TransactionType;
import lombok.extern.slf4j.Slf4j;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

import static com.food.ordering.system.domain.DomainConstants.UTCBRU;

@Slf4j
public class PaymentDomainServiceImpl implements PaymentDomainService {

/**
 * @param payment
 * @param creditEntry
 * @param creditHistories
 * @param failureMessages
 * @param paymentCompletedEventDomainEventPublisher
 * @param paymentFailedEventDomainEventPublisher
 * @return
 */
@Override
public PaymentEvent validateAndInitiatePayment(Payment payment,
                                               CreditEntry creditEntry,
                                               List<CreditHistory> creditHistories,
                                               List<String> failureMessages,
                                               DomainEventPublisher<PaymentCompletedEvent> paymentCompletedEventDomainEventPublisher,
                                               DomainEventPublisher<PaymentFailedEvent> paymentFailedEventDomainEventPublisher) {
    payment.validatePayment(failureMessages);
    payment.initializePayment();
    validateCreditEntry(payment, creditEntry, failureMessages);
    subtractCreditEntry(payment, creditEntry);
    updateCreditHistory(payment, creditHistories, TransactionType.DEBIT);
    validateCreditHistory(creditEntry, creditHistories, failureMessages);

    if( failureMessages.isEmpty() ) {
        log.info("Payment initiated for order id: {}", payment.getOrderId().getValue());
        payment.updateStatus(PaymentStatus.COMPLETED);
        return new PaymentCompletedEvent(payment,
        ZonedDateTime.now(ZoneId.of(UTCBRU)),
        paymentCompletedEventDomainEventPublisher);
    } else {
        log.info("Payment failed for order id: {}", payment.getOrderId().getValue());
        payment.updateStatus(PaymentStatus.FAILED);
        return new PaymentFailedEvent(payment,
        ZonedDateTime.now(ZoneId.of(UTCBRU)),
        failureMessages,
        paymentFailedEventDomainEventPublisher);
    }
}


/**
 * Given that the payment objects are in the correct state this method will not error, that is the credit history for
 * the customer <i>is</i> equal to the current credit for this customer id <i>and</i> totalDebitHistory <i>is not</i>
 * greater than totalCreditHistory.
 *
 * @param creditEntry     the CreditEntry to be validated
 * @param creditHistories a List of credit histories belonging to the customer this CreditEntry relates to
 * @param failureMessages at point of entry an empty List of failureMessages if the CreditHistory to be validated
 *                        fails the logic of this method the list will no longer be empty.
 */
private void validateCreditHistory(CreditEntry creditEntry,
                                   List<CreditHistory> creditHistories,
                                   List<String> failureMessages) {
    Money totalCreditHistory = getTotalHistoryAmount(creditHistories, TransactionType.CREDIT);
    Money totalDebitHistory = getTotalHistoryAmount(creditHistories, TransactionType.DEBIT);

    if( totalDebitHistory.isGreaterThan(totalCreditHistory) ) {

        log.error("Customer with id: {} doesn't have enough credit according to credit history!",
        creditEntry.getCustomerId().getValue());

        failureMessages.add(String.format("Customer with id= %s doesn't have enough credit according to credit " +
        "history!",
        creditEntry.getCustomerId().getValue().toString()));
    }

    if( !creditEntry.getTotalCreditAmount().equals(totalCreditHistory.subtract(totalDebitHistory)) ) {

        log.error("Credit history total is not equal to current credit for customer id: {}!",
        creditEntry.getCustomerId().getValue());

        failureMessages.add(String.format("Credit history total is not equal to current credit for customer id %s!",
        creditEntry.getCustomerId().getValue().toString()));

    }
}

private Money getTotalHistoryAmount(List<CreditHistory> creditHistories, TransactionType transactionType) {
    return creditHistories
    .stream()
    .filter(creditHistory -> transactionType == creditHistory.getTransactionType())
    .map(CreditHistory::getAmount)
    .reduce(Money.ZERO, Money::add);

}


/**
 * Cancellation success:
 * Given the payment has been cancelled the amount debited must be reimbursed/credited back to the customer's
 * CreditEntry and their CreditHistory  updated to reflect this -> PaymentCancelledEvent returned
 * Cancellation failure:
 *
 * @param payment                                   the payment to be validated and cancelled
 * @param creditEntry                               the creditEntry (and so credit amount via creditEntry
 *                                                  .getTotalCreditAmount()) that is to be
 *                                                  validated and cancelled
 * @param creditHistories                           a List of credit histories belonging to the customer this
 *                                                  CreditEntry relates to
 * @param failureMessages                           at point of entry an empty List of failureMessages if the
 *                                                  CreditHistory to be validated
 *                                                  fails the logic of this method the list will no longer be empty.
 * @param paymentCancelledEventDomainEventPublisher
 * @param paymentFailedEventDomainEventPublisher
 * @return PaymentEvent -> PaymentCancelledEvent || PaymentFailedEvent
 */
@Override
public PaymentEvent validateAndCancelPayment(Payment payment,
                                             CreditEntry creditEntry,
                                             List<CreditHistory> creditHistories,
                                             List<String> failureMessages,
                                             DomainEventPublisher<PaymentCancelledEvent> paymentCancelledEventDomainEventPublisher,
                                             DomainEventPublisher<PaymentFailedEvent> paymentFailedEventDomainEventPublisher) {
    payment.validatePayment(failureMessages);
    addCreditEntry(payment, creditEntry);
    updateCreditHistory(payment, creditHistories, TransactionType.CREDIT);

    if( failureMessages.isEmpty() ) {
        log.info("Payment is cancelled for order id: {}", payment.getOrderId().getValue());

        payment.updateStatus(PaymentStatus.CANCELLED);
        return new PaymentCancelledEvent(payment,
        ZonedDateTime.now(ZoneId.of(UTCBRU)),
        paymentCancelledEventDomainEventPublisher);
    } else {
        log.info("Payment cancellation failed for order id: {}", payment.getOrderId().getValue());

        payment.updateStatus(PaymentStatus.FAILED);
        return new PaymentFailedEvent(payment,
        ZonedDateTime.now(ZoneId.of(UTCBRU)),
        failureMessages,
        paymentFailedEventDomainEventPublisher);
    }
}

private void addCreditEntry(Payment payment, CreditEntry creditEntry) {
    creditEntry.addCreditAmount(payment.getPrice());
}

private void validateCreditEntry(Payment payment, CreditEntry creditEntry, List<String> failureMessages) {

    if( payment.getPrice().isGreaterThan(creditEntry.getTotalCreditAmount()) ) {

        log.error("Customer with id: {} doesn't have enough credit for payment!", payment.getCustomerId().getValue());

        failureMessages.add(String.format("Customer with id= %s doesn't have enough credit for payment!",
        payment.getCustomerId().getValue().toString()));
    }
}

private void subtractCreditEntry(Payment payment, CreditEntry creditEntry) {
    creditEntry.subtractCreditAmount(payment.getPrice());
}

private void updateCreditHistory(Payment payment,
                                 List<CreditHistory> creditHistories,
                                 TransactionType transactionType) {
    creditHistories.add(CreditHistory
    .builder()
    .creditHistoryId(new CreditHistoryId(UUID.randomUUID()))
    .customerId(payment.getCustomerId())
    .amount(payment.getPrice())
    .transactionType(transactionType)
    .build());
}


}