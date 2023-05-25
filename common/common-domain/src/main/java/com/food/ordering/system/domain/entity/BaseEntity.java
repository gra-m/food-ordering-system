package com.food.ordering.system.domain.entity;

/**
 * For entities used to service domain core == internal so get/set
 * @param <ID>
 */
public abstract class BaseEntity<ID> {
      private ID id;

public ID getId() {
      return id;
}

public void setId(ID id) {
      this.id = id;
}
}
