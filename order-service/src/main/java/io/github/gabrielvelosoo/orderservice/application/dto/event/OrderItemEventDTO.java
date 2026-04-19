package io.github.gabrielvelosoo.orderservice.application.dto.event;

public record OrderItemEventDTO(
        Long productId,
        Integer quantity
    ) {
}
