package com.food.ordering.system.order.service.dataaccess.restaurant.entity;
import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "products")
@Entity
public class ProductEntity {

@Id
private UUID productId;
private String name;
private BigDecimal price;

@ManyToOne(cascade = CascadeType.ALL)
@JoinColumn(name = "RESTAURANT_ID")
private RestaurantEntity restaurant;

@Override
public boolean equals(Object o) {
      if( this == o ) return true;
      if( o == null || getClass() != o.getClass() ) return false;
      ProductEntity that = ( ProductEntity ) o;
      return Objects.equals(productId, that.productId);
}

@Override
public int hashCode() {
      return Objects.hash(productId);
}
}
