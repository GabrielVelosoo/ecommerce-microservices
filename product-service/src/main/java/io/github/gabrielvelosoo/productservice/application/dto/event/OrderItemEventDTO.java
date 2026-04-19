package io.github.gabrielvelosoo.productservice.application.dto.event;

public record OrderItemEventDTO(
        Long productId,
        Integer quantity
    ) {
}
