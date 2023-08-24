package com.food.ordering.system.order.service.domain.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Used to load the config data from the "order-service" prefix found within e.g. application.yml [todo app.yml?]
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "order-service")
public class OrderServiceConfigData {

private String paymentRequestTopicName;
private String paymentResponseTopicName;
private String restaurantApprovalRequestTopicName;
private String restaurantApprovalResponseTopicName;
// Required? todo
private Integer outboxSchedulerFixedRate;
private Integer outboxSchedulerInitialDelay;


}
