package com.food.ordering.system.order.service.dataaccess.restaurant.adapter;

import com.food.ordering.system.order.service.dataaccess.restaurant.mapper.RestaurantDataAccessMapper;
import com.food.ordering.system.order.service.dataaccess.restaurant.repository.RestaurantJpaRepository;
import com.food.ordering.system.order.service.domain.entity.Restaurant;
import com.food.ordering.system.order.service.domain.ports.output.repository.RestaurantRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class RestaurantRepositoryImpl implements RestaurantRepository {

private final RestaurantDataAccessMapper restaurantDataAccessMapper;
private final RestaurantJpaRepository restaurantJpaRepository;

public RestaurantRepositoryImpl(RestaurantDataAccessMapper restaurantDataAccessMapper,
                                RestaurantJpaRepository restaurantJpaRepository) {
      this.restaurantDataAccessMapper = restaurantDataAccessMapper;
      this.restaurantJpaRepository = restaurantJpaRepository;
}

/**
 * @param restaurant 
 * @return
 */
@Override
public Optional<Restaurant> findRestaurantInformation(Restaurant restaurant) {
      return Optional.empty();
}

}
