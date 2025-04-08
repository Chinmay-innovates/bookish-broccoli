package com.microservices.order.domain.models;

import java.time.LocalDateTime;
import java.util.Set;

public record OrderCreated(
        String eventId,
        String orderNumber,
        Set<OrderItem> items,
        Customer customer,
        Address deliveryAddress,
        LocalDateTime createdAt) {}
