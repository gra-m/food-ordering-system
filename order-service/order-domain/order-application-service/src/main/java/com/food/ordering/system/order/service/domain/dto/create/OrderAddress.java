package com.food.ordering.system.order.service.domain.dto.create;


import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class OrderAddress {
@NotNull
@Max(value = 50)
private final String street;
@NotNull
@Max(value = 10)
private final String postalCode;
@NotNull
private final String city;


}
