package com.food.ordering.system.domain.entity;

/**
 * Used to distinguish Aggregate Roots from normal entities
 */
public abstract class AggregateRoot<BaseId> extends BaseEntity<BaseId> {
}
