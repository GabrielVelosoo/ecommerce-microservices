package io.github.gabrielvelosoo.orderservice.application.usecase;

import io.github.gabrielvelosoo.orderservice.application.dto.event.OrderItemRemovedEvent;
import io.github.gabrielvelosoo.orderservice.application.validator.custom.OrderValidator;
import io.github.gabrielvelosoo.orderservice.domain.entity.Order;
import io.github.gabrielvelosoo.orderservice.domain.entity.OrderItem;
import io.github.gabrielvelosoo.orderservice.domain.service.AuthService;
import io.github.gabrielvelosoo.orderservice.domain.service.OrderService;
import io.github.gabrielvelosoo.orderservice.infrastructure.messaging.OrderEventPublisher;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class RemoveOrderItemUseCase {

    private static final Logger logger = LogManager.getLogger(RemoveOrderItemUseCase.class);

    private final OrderService orderService;
    private final OrderValidator orderValidator;
    private final AuthService authService;
    private final OrderEventPublisher orderEventPublisher;

    public RemoveOrderItemUseCase(OrderService orderService, OrderValidator orderValidator, AuthService authService, OrderEventPublisher orderEventPublisher) {
        this.orderService = orderService;
        this.orderValidator = orderValidator;
        this.authService = authService;
        this.orderEventPublisher = orderEventPublisher;
    }

    @Transactional
    public void execute(Long orderId, Long productId) {
        logger.info("[RemoveOrderItem] Starting removal. orderId='{}', productId='{}'", orderId, productId);
        Order order = orderService.findById(orderId);
        orderValidator.validateOnRemoveItem(order, authService.getUserId());
        OrderItem removedItem = order.removeItem(productId);
        Order savedOrder = orderService.save(order);
        OrderItemRemovedEvent event = new OrderItemRemovedEvent(
                savedOrder.getId(),
                removedItem.getProductId(),
                removedItem.getQuantity()
        );
        orderEventPublisher.publishOrderItemRemovedEvent(event);
        logger.info("[RemoveOrderItem] Item removed successfully. orderId='{}', productId='{}', quantity='{}'",
                orderId, productId, removedItem.getQuantity());
    }
}
