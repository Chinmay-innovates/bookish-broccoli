package com.microservices.order.domain.models;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

class OrderMapper {
    static OrderEntity convertToEntity(CreateOrderRequest request) {
        OrderEntity order = new OrderEntity();
        order.setOrderNumber(UUID.randomUUID().toString());
        order.setStatus(OrderStatus.NEW);
        order.setCustomer(request.customer());
        order.setDeliveryAddress(request.deliveryAddress());
        Set<OrderItemEntity> orderItems = new HashSet<>();
        for (OrderItem item : request.items()) {
            OrderItemEntity orderItem = new OrderItemEntity();
            orderItem.setCode(item.code());
            orderItem.setName(item.name());
            orderItem.setPrice(item.price());
            orderItem.setQuantity(item.quantity());
            orderItem.setOrder(order);
            orderItems.add(orderItem);
        }
        order.setItems(orderItems);
        return order;
    }
}
