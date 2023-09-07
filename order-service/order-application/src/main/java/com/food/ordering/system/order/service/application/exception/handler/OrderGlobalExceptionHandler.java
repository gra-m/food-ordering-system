package com.food.ordering.system.order.service.application.exception.handler;


import com.food.ordering.system.application.handler.ErrorDTO;
import com.food.ordering.system.application.handler.GlobalExceptionHandler;
import com.food.ordering.system.order.service.domain.exception.OrderDomainException;
import com.food.ordering.system.order.service.domain.exception.OrderNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Given that an exception of the given kind shown in @ExceptionHandler is thrown, handle it with this
 * OrderGlobalExceptionHandler class
 * <a href="https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/bind/annotation/ControllerAdvice.html">@ControllerAdvice</a>
 */
@Slf4j
@ControllerAdvice
public class OrderGlobalExceptionHandler extends GlobalExceptionHandler
{

    /**
     * Given that an exception of OrderDomainException.class is thrown return @ResponseStatus BAD_REQUEST and an
     * ErrorDTO
     * wrapped as a html response via use of @ResponseBody
     *
     * @param orderDomainException has been thrown from within order domain
     * @return ErrorDTO
     */
    @ResponseBody
    @ExceptionHandler(value = {OrderDomainException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDTO handleException(OrderDomainException orderDomainException)
    {

        log.error(orderDomainException.getMessage(), orderDomainException);

        return ErrorDTO
                .builder()
                .code(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(orderDomainException.getMessage())
                .build();
    }

    @ResponseBody
    @ExceptionHandler(value = {OrderNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorDTO handleException(OrderNotFoundException orderNotFoundException)
    {

        log.error(orderNotFoundException.getMessage(), orderNotFoundException);

        return ErrorDTO
                .builder()
                .code(HttpStatus.NOT_FOUND.getReasonPhrase())
                .message(orderNotFoundException.getMessage())
                .build();
    }


}
