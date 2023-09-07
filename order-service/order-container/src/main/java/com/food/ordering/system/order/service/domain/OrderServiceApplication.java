package com.food.ordering.system.order.service.domain;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * note:
 * SpringBootApplication
 * scanBasePackages: important when working with multiple modules any module in project will be scanned by Spring as
 * long as it starts with "com.food.ordering.system".
 * EntityScan
 * basePackages: requirement for multimodule project the jpa entity classes are strictly held in the dataaccess package
 * EnableJpaRepositories
 * basePackages: dataaccess repos are also strictly held in dataacess package
 * <p>
 * Note: following the move of common restaurant dataaccess code to new module common/common-dataaccess:
 * new basePackage scan required:
 */
@EnableJpaRepositories(basePackages = {"com.food.ordering.system.order.service.dataaccess", "com.food.ordering.system" +
        ".dataaccess"})
@EntityScan(basePackages = {"com.food.ordering.system.order.service.dataaccess", "com.food.ordering.system.dataaccess"})
@SpringBootApplication(scanBasePackages = "com.food.ordering.system")
public class OrderServiceApplication
{


    /**
     * This class and any args are passed to SpringApplication.run
     *
     * @param args as required for the business logic of this Spring microservice
     */
    public static void main(String[] args)
    {
        SpringApplication.run(OrderServiceApplication.class, args);
    }


}
