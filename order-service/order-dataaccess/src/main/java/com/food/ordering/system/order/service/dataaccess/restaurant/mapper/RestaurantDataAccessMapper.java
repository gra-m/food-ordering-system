package com.food.ordering.system.order.service.dataaccess.restaurant.mapper;

import com.food.ordering.system.dataaccess.restaurant.entity.RestaurantEntity;
import com.food.ordering.system.dataaccess.restaurant.exception.RestaurantDataAccessException;
import com.food.ordering.system.domain.valueobject.Money;
import com.food.ordering.system.domain.valueobject.ProductId;
import com.food.ordering.system.domain.valueobject.RestaurantId;
import com.food.ordering.system.order.service.domain.entity.Product;
import com.food.ordering.system.order.service.domain.entity.Restaurant;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class RestaurantDataAccessMapper {

/**
 * Given a restaurant, returns a list of its product UUID
 *
 * @param restaurant that a list of product UUID's is required for
 * @return List of productIDs
 */
public List<UUID> restaurantToRestaurantProducts(Restaurant restaurant) {
    return restaurant.getProducts().stream().map(product -> product.getId().getValue()).collect(Collectors.toList());
}

/**
 * <p>Passed a list of RestaurantEntities (from the same restaurant) this method retrieves the first or throws an
 * exception if empty.</p><p>It then creates a list of Products from the List[RestaurantEntity]</p>
 * <p>and finally returns a Restaurant domain object containing this list of products.</p>
 *
 * @param restaurantEntities a list of unique Restaurant and that restaurant's products
 * @return a Restaurant
 */
public Restaurant restaurantEntityToRestaurant(List<RestaurantEntity> restaurantEntities) {

    RestaurantEntity restaurantEntity = restaurantEntities
    .stream()
    .findFirst()
    .orElseThrow(() -> new RestaurantDataAccessException("Restaurant could not be found!"));

    List<Product> restaurantProducts = restaurantEntities
    .stream()
    .map(entity -> new Product(new ProductId(entity.getProductId()),
    entity.getProductName(),
    new Money(entity.getProductPrice())))
    .toList();

    return Restaurant
    .builder()
    .restaurantId(new RestaurantId(restaurantEntity.getRestaurantId()))
    .products(restaurantProducts)
    .active(restaurantEntity.getRestaurantActive())
    .build();
}


}
