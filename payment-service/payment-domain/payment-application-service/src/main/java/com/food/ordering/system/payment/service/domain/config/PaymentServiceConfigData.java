package com.food.ordering.system.payment.service.domain.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Given application.yml exists and prefix "payment-service" can be found where expected these fields will be
 * configured/Initialised with their names;
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "payment-service")
public class PaymentServiceConfigData
{
    private String paymentRequestTopicName;
    private String paymentResponseTopicName;


}
