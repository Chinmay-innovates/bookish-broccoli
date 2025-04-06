package com.microservices.order.domain.models;

import com.microservices.order.clients.catalog.Product;
import com.microservices.order.clients.catalog.ProductServiceClient;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
class OrderValidator {
    private static final Logger logger = LoggerFactory.getLogger(OrderValidator.class);
    private final ProductServiceClient client;

    OrderValidator(ProductServiceClient client) {
        this.client = client;
    }

    void validate(CreateOrderRequest request) {
        Set<OrderItem> items = request.items();
        for (OrderItem item : items) {
            Product product = client.getProductByCode(item.code())
                    .orElseThrow(() -> new InvalidOrderException("Invalid product code: " + item.code()));
            if (item.price().compareTo(product.price()) != 0) {
                logger.error(
                        "Product price not matched for item code: {}, Actual price:{}, Received price:{}",
                        item.code(),
                        product.price(),
                        item.price());
                throw new InvalidOrderException("Product price not matching");
            }
        }
    }
}
