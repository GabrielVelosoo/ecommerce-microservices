package io.github.gabrielvelosoo.orderservice.infrastructure.messaging;

import io.github.gabrielvelosoo.orderservice.application.dto.event.OrderCancelledEvent;
import io.github.gabrielvelosoo.orderservice.application.dto.event.OrderCreatedEvent;
import io.github.gabrielvelosoo.orderservice.application.dto.event.OrderItemRemovedEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class OrderEventPublisher {

    private static final Logger logger = LogManager.getLogger(OrderEventPublisher.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public OrderEventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishOrderCreated(OrderCreatedEvent event) {
        logger.info("[Kafka] Publishing OrderCreatedEvent. orderId='{}'", event.orderId());
        kafkaTemplate.send(
                "order-created-topic",
                event.orderId().toString(),
                event
        );
    }

    public void publishOrderCancelled(OrderCancelledEvent event) {
        logger.info("[Kafka] Publishing OrderCancelledEvent. orderId='{}'", event.orderId());
        kafkaTemplate.send(
                "order-cancelled-topic",
                event.orderId().toString(),
                event
        );
    }

    public void publishOrderItemRemovedEvent(OrderItemRemovedEvent event) {
        logger.info("[Kafka] Publishing OrderItemRemovedEvent. orderId='{}'", event.orderId());
        kafkaTemplate.send(
                "order-item-removed-topic",
                event.orderId().toString(),
                event
        );
    }
}
