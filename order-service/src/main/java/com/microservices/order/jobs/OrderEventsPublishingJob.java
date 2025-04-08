package com.microservices.order.jobs;

import com.microservices.order.domain.models.OrderEventService;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
class OrderEventsPublishingJob {
    private static final Logger logger = LoggerFactory.getLogger(OrderEventsPublishingJob.class);

    private final OrderEventService orderEventService;

    OrderEventsPublishingJob(OrderEventService orderEventService) {
        this.orderEventService = orderEventService;
    }

    @Scheduled(cron = "${orders.publish-order-events-cron-job}")
    public void publishOrderEvents() {
        logger.info("Publishing order events at {} ", Instant.now());
        orderEventService.publishOrderEvents();
    }
}
