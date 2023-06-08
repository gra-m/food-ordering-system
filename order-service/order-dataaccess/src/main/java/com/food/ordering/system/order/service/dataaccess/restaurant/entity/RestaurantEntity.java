package com.food.ordering.system.order.service.dataaccess.restaurant.entity;

import lombok.*;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 *
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "restaurants")
@Entity
public class RestaurantEntity {

@Id
private UUID id;
private Boolean active;

@OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL)
private List<ProductEntity> products;

@Override
public boolean equals(Object o) {
      if( this == o ) return true;
      if( o == null || getClass() != o.getClass() ) return false;
      RestaurantEntity that = ( RestaurantEntity ) o;
      return Objects.equals(id, that.id);
}

@Override
public int hashCode() {
      return Objects.hash(id);
}
}
