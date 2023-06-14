package com.food.ordering.system.customer.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * note:
 * SpringBootApplication
 * scanBasePackages: important when working with multiple modules any module in project will be scanned by Spring as
 * long as it starts with "com.food.ordering.system".
 *
 * fixme below needed?
 * EntityScan
 * basePackages: requirement for multimodule project the jpa entity classes are strictly held in the dataaccess package
 * EnableJpaRepositories
 * basePackages: dataaccess repos are also strictly held in dataacess package
 *
 */


@SpringBootApplication(scanBasePackages = "com.food.ordering.system")
public class CustomerServiceApplication {

/**
 * This class and any args are passed to SpringApplication.run to start the application
 *
 * @param args as required for the business logic of this Spring microservice tbc
 */
public static void main(String[] args) {
    SpringApplication.run(CustomerServiceApplication.class, args);
}
}
