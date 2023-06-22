package com.food.ordering.system.order.service.dataaccess.restaurant.adapter;

import com.food.ordering.system.dataaccess.restaurant.entity.RestaurantEntity;
import com.food.ordering.system.dataaccess.restaurant.repository.RestaurantJpaRepository;
import com.food.ordering.system.order.service.dataaccess.restaurant.mapper.RestaurantDataAccessMapper;
import com.food.ordering.system.order.service.domain.entity.Restaurant;
import com.food.ordering.system.order.service.domain.ports.output.repository.RestaurantRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * RestaurantRepository [I] is an output port in the domain layer
 */
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
 * <p>First retrieve a list of product UUID's for a restaurant</p>
 * <p>Then retrieve a list of restaurantEntities that have the restaurant id / product id match</p>
 * <p>[The IN operator is a shorthand for multiple OR conditions.]</p>
 * <p>Then return a restaurant domain object with a list of all of its products</p>
 * @param restaurant the restaurant info is requested on
 * @return an optional restaurant with all of its live, available products.
 */
@Override
public Optional<Restaurant> findRestaurantInformation(Restaurant restaurant) {
      List<UUID> restaurantProducts =
          restaurantDataAccessMapper.restaurantToRestaurantProducts(restaurant);

      Optional<List<RestaurantEntity>> restaurantEntities = restaurantJpaRepository
          .findByRestaurantIdAndProductIdIn(restaurant.getId().getValue(), restaurantProducts);

      return restaurantEntities.map(restaurantDataAccessMapper::restaurantEntityToRestaurant);
}

}
