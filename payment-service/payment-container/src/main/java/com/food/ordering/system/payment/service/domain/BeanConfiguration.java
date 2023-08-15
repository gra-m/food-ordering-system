package com.food.ordering.system.payment.service.domain;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * This is required because of the purposeful independence of the payment-domain-service module.
 * Because it does not depend on Spring it has to be made available as a Spring Bean through @Configuration
 */
@Configuration
public class BeanConfiguration {

@Bean
public PaymentDomainService paymentDomainService() {
    return new PaymentDomainServiceImpl();
}


}
