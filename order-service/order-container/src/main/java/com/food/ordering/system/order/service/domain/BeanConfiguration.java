package com.food.ordering.system.order.service.domain;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring managed configuration bean
 * In order to keep OrderDomainService [in fact the Domain Core of the entire order service]  independent from
 * external dependencies it is instead tagged as SpringBean here.
 */

@Configuration
public class BeanConfiguration {

/**
 * Registering OrderDomainService as a SpringBean
 *
 * @return SpringBean OrderDomainService
 */
@Bean
public OrderDomainService orderDomainService() {
    return new OrderDomainServiceImpl();
}


}
