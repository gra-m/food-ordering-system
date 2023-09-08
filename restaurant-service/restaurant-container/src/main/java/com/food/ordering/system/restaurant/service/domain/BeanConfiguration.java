package com.food.ordering.system.restaurant.service.domain;

import com.food.ordering.system.restaurant.service.dataaccess.restaurant.outbox.adapter.OrderOutboxRepositoryImpl;
import com.food.ordering.system.restaurant.service.dataaccess.restaurant.outbox.repository.OrderOutboxJpaRepository;
import com.food.ordering.system.restaurant.service.domain.ports.output.repository.OrderOutboxRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration
{

    @Bean
    public RestaurantDomainService restaurantDomainService()
    {
        return new RestaurantDomainServiceImpl();
    }


}
