package com.food.ordering.system.domain.valueobject;


import java.util.Objects;

/**<h3> BaseId of type T gives a lot of flexibility, order id's will not only have to be e.g. UUIDs.
 *
 * <p>Also.. you do not have to implement private final T value on each class</p>
 *
 * <p>ON: Value Objects <br> give context to what they contain, and may contain useful business logic
 * that may be out of place elsewhere.
 * @param <T>
 */
public abstract class BaseId<T> {
      private final T value;

/**
 * Subclass only access
 * @param value generic T will be set as whatever it is in the extending subclass.
 */
protected BaseId(T value) {
            this.value = value;
      }

/**
 *
 * @return the value as whatever T has been set in a subclass.
 */
public T getValue() {
      return value;
}


/**
 * Best practice to implement this
 * @param o any object passed for comparison
 * @return true if objects are equal
 */
@Override
public boolean equals(Object o) {
      if( this == o ) return true;
      if( o == null || getClass() != o.getClass() ) return false;
      BaseId<?> baseId = ( BaseId<?> ) o;
      return Objects.equals(value, baseId.value);
}

/**
 * Best practice to implement this,
 *
 */
@Override
public int hashCode() {
      return Objects.hash(value);
}
}
