package com.food.ordering.system.domain.event;

/**
 * The Singleton below results in the same instance of EmptyEvent being used everywhere, this is not an issue, it
 * can be used in different clusters as it is only a marker class. Don't user for anything else.
 * <p>
 * Used in implementation of SAGA when the final process(T data) still has to return an event, and for the first step
 * rollback event.
 */
public final class EmptyEvent implements DomainEvent<Void> {
public static final EmptyEvent INSTANCE = new EmptyEvent();

private EmptyEvent() {
}

@Override
public void fire() {
}


}
