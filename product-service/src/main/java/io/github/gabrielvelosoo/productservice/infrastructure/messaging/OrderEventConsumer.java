package io.github.gabrielvelosoo.productservice.infrastructure.messaging;

import io.github.gabrielvelosoo.productservice.application.dto.event.OrderCancelledEvent;
import io.github.gabrielvelosoo.productservice.application.dto.event.OrderCreatedEvent;
import io.github.gabrielvelosoo.productservice.application.dto.event.OrderItemRemovedEvent;
import io.github.gabrielvelosoo.productservice.application.usecase.event.HandleOrderCancelledUseCase;
import io.github.gabrielvelosoo.productservice.application.usecase.event.HandleOrderCreatedUseCase;
import io.github.gabrielvelosoo.productservice.application.usecase.event.HandleOrderItemRemovedUseCase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Profile("!test")
public class OrderEventConsumer {

    private static final Logger logger = LogManager.getLogger(OrderEventConsumer.class);

    private final HandleOrderCreatedUseCase handleOrderCreated;
    private final HandleOrderCancelledUseCase handleOrderCancelled;
    private final HandleOrderItemRemovedUseCase handleOrderItemRemoved;

    public OrderEventConsumer(HandleOrderCreatedUseCase handleOrderCreated, HandleOrderCancelledUseCase handleOrderCancelled, HandleOrderItemRemovedUseCase handleOrderItemRemoved) {
        this.handleOrderCreated = handleOrderCreated;
        this.handleOrderCancelled = handleOrderCancelled;
        this.handleOrderItemRemoved = handleOrderItemRemoved;
    }

    @KafkaListener(
            topics = "order-created-topic",
            groupId = "product-service",
            containerFactory = "orderCreatedKafkaListenerContainerFactory"
    )
    public void onOrderCreated(OrderCreatedEvent event) {
        logger.info("[KAFKA] Received OrderCreatedEvent. orderId='{}'", event.orderId());
        handleOrderCreated.execute(event);
    }

    @KafkaListener(
            topics = "order-cancelled-topic",
            groupId = "product-service",
            containerFactory = "orderCancelledKafkaListenerContainerFactory"
    )
    public void onOrderCancelled(OrderCancelledEvent event) {
        logger.info("[KAFKA] Received OrderCancelledEvent. orderId='{}'", event.orderId());
        handleOrderCancelled.execute(event);
    }

    @KafkaListener(
            topics = "order-item-removed-topic",
            groupId = "product-service",
            containerFactory = "orderItemRemovedKafkaListenerContainerFactory"
    )
    public void onRemoveOrderItem(OrderItemRemovedEvent event) {
        logger.info("[KAFKA] Received OrderItemRemovedEvent. orderId='{}'", event.orderId());
        handleOrderItemRemoved.execute(event);
    }
}
