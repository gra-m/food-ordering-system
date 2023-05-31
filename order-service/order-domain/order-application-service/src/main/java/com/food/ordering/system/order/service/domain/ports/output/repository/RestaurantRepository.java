package com.food.ordering.system.order.service.domain.ports.output.repository;

import com.food.ordering.system.order.service.domain.entity.Restaurant;

import java.util.Optional;

/**
 * Repository interfaces will be implemented in the data access layer via adapters
 */
public interface RestaurantRepository {
Optional<Restaurant> findRestaurantInformation(Restaurant restaurant);
}
