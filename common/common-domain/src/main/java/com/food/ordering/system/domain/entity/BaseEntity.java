package com.food.ordering.system.domain.entity;

<<<<<<< Updated upstream
=======
import lombok.Getter;
import lombok.Setter;
>>>>>>> Stashed changes

import java.util.Objects;

/** Note this was first set up with ID, but why? after setting up BaseId for all valueItem Ids...
 * For entities used to service domain core == internal so get/set
 * @param <BaseId>>
 */
public abstract class BaseEntity<BaseId> {
      private BaseId id;

<<<<<<< Updated upstream
public BaseId getId() {
      return this.id;
}

public void setId(BaseId id) {
      this.id = id;
=======
public ID getId() {
    return this.id;
}
public void setId(ID id) {
    this.id = id;
>>>>>>> Stashed changes
}



/**
 * Used across the board for all extending classes, saving having separate in each entity.
 * @param o the object to test equality with
 * @return true if ids are the same
 */
@Override
public boolean equals(Object o) {
      if( this == o ) return true;
      if( o == null || getClass() != o.getClass() ) return false;
      BaseEntity<?> that = ( BaseEntity<?> ) o;
      return Objects.equals(id, that.id);
}

@Override
public int hashCode() {
      return Objects.hash(id);
}
}
