package com.food.ordering.system.order.service.domain.ports.output.repository;

import com.food.ordering.system.order.service.domain.entity.Customer;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository interfaces will be implemented in the data access layer via adapters
 */
public interface CustomerRepository
{

    Optional<Customer> findCustomer(UUID customerId);


}
