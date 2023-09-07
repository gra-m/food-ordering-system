package com.food.ordering.system.payment.service.messaging.listener.kafka;

import com.food.ordering.system.domain.valueobject.PaymentOrderStatus;
import com.food.ordering.system.kafka.consumer.KafkaConsumer;
import com.food.ordering.system.kafka.order.avro.model.PaymentRequestAvroModel;
import com.food.ordering.system.payment.service.domain.exception.PaymentApplicationServiceException;
import com.food.ordering.system.payment.service.domain.exception.PaymentNotFoundException;
import com.food.ordering.system.payment.service.domain.ports.input.message.listener.PaymentRequestMessageListener;
import com.food.ordering.system.payment.service.messaging.mapper.PaymentMessagingDataMapper;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.util.PSQLState;
import org.springframework.dao.DataAccessException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.List;

/**
 * Listen for kafka messages and trigger using PaymentRequestMessageListenerImpl
 */
@Slf4j
@Component
public class PaymentRequestKafkaListener implements KafkaConsumer<PaymentRequestAvroModel>
{

    private final PaymentRequestMessageListener paymentRequestMessageListener;
    private final PaymentMessagingDataMapper paymentMessagingDataMapper;

    public PaymentRequestKafkaListener(PaymentRequestMessageListener paymentRequestMessageListener,
                                       PaymentMessagingDataMapper paymentMessagingDataMapper)
    {
        this.paymentRequestMessageListener = paymentRequestMessageListener;
        this.paymentMessagingDataMapper = paymentMessagingDataMapper;
    }


    /**
     * @param messages   A list of given Type of messages == Spring.messaging.handler.annotation @Payload
     * @param keys       A list of Longs that are keys == Spring @Header
     * @param partitions A list of Integers that are the partitions == Spring @Header
     * @param offsets    A list of Longs that are the offsets == Spring @Header with KafkaHeaders SpringKafkaHelper
     * @KafkaListener as per configured in payment-container module application.yml
     * <p>
     * PENDING or CANCELLED if failed in approval phase (payment or restaurant order-fulfillment issue)
     * <p>
     * PaymentRequestMessageListenerImpl in the payment-application-service fires the required events <i>after</i>
     * the data has been persisted in the relevant local database.
     * payment-service/payment-domain/payment-application-service/src/main/java/com/food/ordering/system/payment
     * /service/domain/ports/input/message/listener/PaymentRequestMessageListenerImpl.java
     */
    @Override
    @KafkaListener(id = "${kafka-consumer-config.payment-consumer-group-id}", topics = "${payment-service.payment" +
            "-request-topic-name}")
    public void receive(@Payload List<PaymentRequestAvroModel> messages,
                        @Header(KafkaHeaders.RECEIVED_KEY) List<String> keys,
                        @Header(KafkaHeaders.RECEIVED_PARTITION) List<Integer> partitions,
                        @Header(KafkaHeaders.OFFSET) List<Long> offsets)
    {

        log.info("{} number of payment requests received with keys: {}, partitions: {} and, offsets: {}",
                messages.size(),
                keys.toString(),
                partitions.toString(),
                offsets.toString());

        //Changed to .equals name() here as well
        messages.forEach(paymentRequestAvroModel ->
        {
            try {
                if (paymentRequestAvroModel.getPaymentOrderStatus().name().equals(PaymentOrderStatus.PENDING.name())) {
                    log.info("Processing payment for order id: {}", paymentRequestAvroModel.getOrderId());

                    paymentRequestMessageListener.completePayment(paymentMessagingDataMapper.paymentRequestAvroModelToPaymentRequest(
                            paymentRequestAvroModel));

                } else if (paymentRequestAvroModel
                        .getPaymentOrderStatus()
                        .name()
                        .equals(PaymentOrderStatus.CANCELLED.name())) {
                    log.info("Cancelling payment for order id: {}", paymentRequestAvroModel.getOrderId());

                    paymentRequestMessageListener.cancelPayment(paymentMessagingDataMapper.paymentRequestAvroModelToPaymentRequest(
                            paymentRequestAvroModel));
                }
            } catch (DataAccessException e) {
                if (isNotUniqueViolation(e, paymentRequestAvroModel.getOrderId())) {
                    throw new PaymentApplicationServiceException("Throwing DataAccessException in " +
                            "PaymentRequestKafkaListener: " + e.getMessage(), e);
                }
            } catch (PaymentNotFoundException e) {
                //NO-OP for PaymentNotFoundException
                log.error("No payment found for order id: {}", paymentRequestAvroModel.getOrderId());
            }

        });

    }

    /**
     * UNIQUE_VIOLATION works with postgres dependency where optimistic locking is not available as there is NO
     * EXISTING RECORD. Instead Ali uses unique constraint on the index and catches if the same operation is tried
     * twice.
     *
     * @param e
     * @param orderId
     * @return
     */
    private boolean isNotUniqueViolation(DataAccessException e, String orderId)
    {
        SQLException sqlException = (SQLException) e.getRootCause();
        if (sqlException != null && sqlException.getSQLState() != null)
            if (sqlException.getSQLState().equals(PSQLState.UNIQUE_VIOLATION.getState())) {
                // NO-OP for unique restraint exception
                log.error("Caught unique constraint exception with sql state: {} in PaymentRequestKafkListener for " +
                                "order id: {}",
                        sqlException.getSQLState(), orderId);
                return false;
            }
        return true;
    }


}
