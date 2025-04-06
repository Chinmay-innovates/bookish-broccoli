package com.microservices.order.domain.models;

public enum OrderStatus {
    NEW,
    IN_PROCESS,
    DELIVERED,
    CANCELLED,
    ERROR
}
