package com.food.ordering.system.domain.entity;

import java.util.Objects;

public abstract class BaseEntity<ID> {
private ID id;

public ID getId() {
    return id;
}

public void setId(ID id) {
    this.id = id;
}

/**
 * Used across the board for all extending classes, saving having separate in each entity.
 *
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
