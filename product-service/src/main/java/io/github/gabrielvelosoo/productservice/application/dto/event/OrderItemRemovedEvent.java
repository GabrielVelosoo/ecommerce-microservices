package io.github.gabrielvelosoo.productservice.application.dto.event;

public record OrderItemRemovedEvent(
        Long orderId,
        Long productId,
        Integer quantity
    ) {
}
