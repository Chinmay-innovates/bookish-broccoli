package com.microservices.order.domain.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class OrderEventService {
    private static final Logger logger = LoggerFactory.getLogger(OrderEventService.class);

    private final OrderEventRepository orderEventRepository;
    private final OrderEventPublisher orderEventPublisher;
    private final ObjectMapper objectMapper;

    OrderEventService(
            OrderEventRepository orderEventRepository,
            ObjectMapper objectMapper,
            OrderEventPublisher orderEventPublisher) {
        this.orderEventRepository = orderEventRepository;
        this.orderEventPublisher = orderEventPublisher;
        this.objectMapper = objectMapper;
    }

    void save(OrderCreatedEvent event) {
        OrderEventEntity orderEventEntity = new OrderEventEntity();
        orderEventEntity.setEventId(event.eventId());
        orderEventEntity.setEventType(OrderEventType.ORDER_CREATED);
        orderEventEntity.setOrderNumber(event.orderNumber());
        orderEventEntity.setCreatedAt(event.createdAt());
        orderEventEntity.setPayload(toJsonPayload(event));
        orderEventRepository.save(orderEventEntity);
    }

    private String toJsonPayload(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private <T> T fromJsonPayload(String json, Class<T> type) {
        try {
            return objectMapper.readValue(json, type);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public void publishOrderEvents() {
        Sort sort = Sort.by(Sort.Direction.ASC, "createdAt");
        List<OrderEventEntity> orderEvents = orderEventRepository.findAll(sort);
        logger.info("Found {} Order events received to be published", orderEvents.size());
        for (OrderEventEntity event : orderEvents) {
            this.publishEvent(event);
            orderEventRepository.delete(event);
        }
    }

    private void publishEvent(OrderEventEntity orderEventEntity) {
        OrderEventType eventType = orderEventEntity.getEventType();
        switch (eventType) {
            case ORDER_CREATED:
                OrderCreatedEvent orderCreatedEvent =
                        fromJsonPayload(orderEventEntity.getPayload(), OrderCreatedEvent.class);
                System.out.println("orderCreatedEvent: " + orderCreatedEvent);
                orderEventPublisher.publish(orderCreatedEvent);
                break;
            default:
                logger.warn("Unsupported event type: {}", eventType);
        }
    }
}
