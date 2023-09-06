package com.food.ordering.system.payment.service.domain;

import com.food.ordering.system.payment.service.domain.entity.CreditEntry;
import com.food.ordering.system.payment.service.domain.entity.CreditHistory;
import com.food.ordering.system.payment.service.domain.entity.Payment;
import com.food.ordering.system.payment.service.domain.event.PaymentEvent;
import java.util.List;

public interface PaymentDomainService {

/**
 * @param payment                                   the payment to be validated and initiated
 * @param creditEntry                               the CreditEntry to be added if valid
 * @param creditHistories                           the CreditHistory to be updated if valid
 * @param failureMessages                           the failure messages for logging of failures
 *                                                  dependencies to domain-core
 * @return a PaymentEvent to be returned, payment events
 */
PaymentEvent validateAndInitiatePayment(Payment payment,
                                        CreditEntry creditEntry,
                                        List<CreditHistory> creditHistories,
                                        List<String> failureMessages);

PaymentEvent validateAndCancelPayment(Payment payment,
                                      CreditEntry creditEntry,
                                      List<CreditHistory> creditHistories,
                                      List<String> failureMessages);


}
