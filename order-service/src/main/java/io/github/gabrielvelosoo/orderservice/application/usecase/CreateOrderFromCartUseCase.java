package io.github.gabrielvelosoo.orderservice.application.usecase;

import io.github.gabrielvelosoo.orderservice.application.dto.event.OrderCreatedEvent;
import io.github.gabrielvelosoo.orderservice.application.dto.order.CreateOrderFromCartRequestDTO;
import io.github.gabrielvelosoo.orderservice.application.dto.order.CreateOrderItemDTO;
import io.github.gabrielvelosoo.orderservice.application.dto.order.OrderResponseDTO;
import io.github.gabrielvelosoo.orderservice.application.mapper.OrderEventMapper;
import io.github.gabrielvelosoo.orderservice.application.mapper.OrderMapper;
import io.github.gabrielvelosoo.orderservice.domain.entity.Order;
import io.github.gabrielvelosoo.orderservice.domain.service.OrderService;
import io.github.gabrielvelosoo.orderservice.infrastructure.messaging.OrderEventPublisher;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class CreateOrderFromCartUseCase {

    private static final Logger logger = LogManager.getLogger(CreateOrderFromCartUseCase.class);

    private final OrderService orderService;
    private final OrderMapper orderMapper;
    private final OrderEventPublisher orderEventPublisher;
    private final OrderEventMapper orderEventMapper;

    public CreateOrderFromCartUseCase(OrderService orderService, OrderMapper orderMapper, OrderEventPublisher orderEventPublisher, OrderEventMapper orderEventMapper) {
        this.orderService = orderService;
        this.orderMapper = orderMapper;
        this.orderEventPublisher = orderEventPublisher;
        this.orderEventMapper = orderEventMapper;
    }

    @Transactional
    public OrderResponseDTO execute(CreateOrderFromCartRequestDTO request) {
        logger.info("[CreateOrderFromCart] Starting order creation from cart. userId='{}', itemsCount='{}'",
                request.userId(), request.items().size());
        Order order = Order.create(request.userId());
        logger.debug("[CreateOrderFromCart] Order built in memory. userId='{}', itemsCount='{}', total='{}'",
                request.userId(), request.items().size(), order.getTotal());
        addItemsToOrder(order, request.items());
        Order savedOrder = orderService.save(order);
        OrderCreatedEvent event = buildOrderCreatedEvent(savedOrder);
        orderEventPublisher.publishOrderCreated(event);
        logger.info("[CreateOrderFromCart] Order successfully created. orderId='{}', total='{}'",
                savedOrder.getId(), savedOrder.getTotal());
        return orderMapper.toDTO(savedOrder);
    }

    private void addItemsToOrder(Order order, List<CreateOrderItemDTO> items) {
        items.forEach(item ->
                order.addItem(
                        item.productId(),
                        item.quantity(),
                        item.price()
                )
        );
    }

    private OrderCreatedEvent buildOrderCreatedEvent(Order order) {
        return new OrderCreatedEvent(
                order.getId(),
                orderEventMapper.toEventItems(order.getItems())
        );
    }
}
