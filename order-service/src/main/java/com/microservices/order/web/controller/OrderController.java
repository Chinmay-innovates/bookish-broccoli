package com.microservices.order.web.controller;

import static org.springframework.http.HttpStatus.CREATED;

import com.microservices.order.domain.models.CreateOrderRequest;
import com.microservices.order.domain.models.CreateOrderResponse;
import com.microservices.order.domain.models.OrderService;
import com.microservices.order.domain.models.SecurityService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
class OrderController {
    private static final Logger log = LoggerFactory.getLogger(OrderController.class);
    private final SecurityService securityService;
    private final OrderService orderService;

    OrderController(SecurityService securityService, OrderService orderService) {
        this.securityService = securityService;
        this.orderService = orderService;
    }

    @PostMapping
    @ResponseStatus(CREATED)
    CreateOrderResponse createOrder(@Valid @RequestBody CreateOrderRequest request) {
        String userName = securityService.getLoginUserName();
        log.info("Creating order for user: {}", userName);
        return orderService.createOrder(userName, request);
    }
}
