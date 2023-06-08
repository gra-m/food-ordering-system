package com.food.ordering.system.order.service.dataaccess.restaurant.mapper;

import com.food.ordering.system.domain.valueobject.Money;
import com.food.ordering.system.domain.valueobject.ProductId;
import com.food.ordering.system.domain.valueobject.RestaurantId;
import com.food.ordering.system.order.service.dataaccess.restaurant.entity.ProductEntity;
import com.food.ordering.system.order.service.dataaccess.restaurant.entity.RestaurantEntity;
import com.food.ordering.system.order.service.domain.entity.Product;
import com.food.ordering.system.order.service.domain.entity.Restaurant;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class RestaurantDataAccessMapper {

public RestaurantEntity restaurantToRestaurantEntity(Restaurant restaurant) {
      RestaurantEntity restaurantEntity = RestaurantEntity
          .builder()
          .id(restaurant.getId().getValue())
          .active(restaurant.isActive())
          .products(productsToProductEntities(restaurant.getProducts()))
          .build();

      restaurantEntity.getProducts().forEach(productEntity -> productEntity.setRestaurant(restaurantEntity));

      return restaurantEntity;
}

public Restaurant restaurantEntityToRestaurant(RestaurantEntity restaurantEntity) {
      return Restaurant
          .builder()
          .restaurantId(new RestaurantId(restaurantEntity.getId()))
          .active(restaurantEntity.getActive())
          .products(productEntitiesToProducts(restaurantEntity.getProducts()))
          .build();

}

private List<Product> productEntitiesToProducts(List<ProductEntity> products) {
      return products
          .stream()
          .map(productEntity -> new Product(new ProductId(productEntity.getProductId()), productEntity.getName(),
              new Money(productEntity.getPrice())))
          .collect(Collectors.toList());
}

private List<ProductEntity> productsToProductEntities(List<Product> products) {
      return products
          .stream()
          .map(product -> ProductEntity
              .builder()
              .productId(product.getId().getValue())
              .name(product.getName())
              .price(product.getPrice().getAmount())
              .build())
          .collect(Collectors.toList());

}
}
