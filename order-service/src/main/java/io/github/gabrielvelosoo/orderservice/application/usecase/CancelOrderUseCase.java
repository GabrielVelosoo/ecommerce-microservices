package io.github.gabrielvelosoo.orderservice.application.usecase;

import io.github.gabrielvelosoo.orderservice.application.dto.event.OrderCancelledEvent;
import io.github.gabrielvelosoo.orderservice.application.mapper.OrderEventMapper;
import io.github.gabrielvelosoo.orderservice.application.validator.custom.OrderValidator;
import io.github.gabrielvelosoo.orderservice.domain.entity.Order;
import io.github.gabrielvelosoo.orderservice.domain.service.AuthService;
import io.github.gabrielvelosoo.orderservice.domain.service.OrderService;
import io.github.gabrielvelosoo.orderservice.infrastructure.messaging.OrderEventPublisher;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CancelOrderUseCase {

    private static final Logger logger = LogManager.getLogger(CancelOrderUseCase.class);

    private final OrderService orderService;
    private final OrderValidator orderValidator;
    private final AuthService authService;
    private final OrderEventPublisher orderEventPublisher;
    private final OrderEventMapper orderEventMapper;

    public CancelOrderUseCase(OrderService orderService, OrderValidator orderValidator, AuthService authService, OrderEventPublisher orderEventPublisher, OrderEventMapper orderEventMapper) {
        this.orderService = orderService;
        this.orderValidator = orderValidator;
        this.authService = authService;
        this.orderEventPublisher = orderEventPublisher;
        this.orderEventMapper = orderEventMapper;
    }

    @Transactional
    public void execute(Long orderId) {
        logger.info("[CancelOrder] Starting cancellation. orderId='{}'", orderId);
        Order order = orderService.findById(orderId);
        orderValidator.validateOnCancelOrder(order, authService.getUserId());
        order.cancel();
        Order savedOrder = orderService.save(order);
        logger.debug("[CancelOrder] Order successfully saved. orderId='{}', status='{}'", savedOrder.getId(), savedOrder.getStatus());
        OrderCancelledEvent event = buildOrderCancelledEvent(savedOrder);
        orderEventPublisher.publishOrderCancelled(event);
        logger.info("[CancelOrder] Order cancelled successfully. orderId='{}'", orderId);
    }

    private OrderCancelledEvent buildOrderCancelledEvent(Order order) {
        return new OrderCancelledEvent(
                order.getId(),
                orderEventMapper.toEventItems(order.getItems())
        );
    }
}
