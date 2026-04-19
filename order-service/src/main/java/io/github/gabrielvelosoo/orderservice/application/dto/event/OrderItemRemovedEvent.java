package io.github.gabrielvelosoo.orderservice.application.dto.event;

public record OrderItemRemovedEvent(
        Long orderId,
        Long productId,
        Integer quantity
    ) {
}
