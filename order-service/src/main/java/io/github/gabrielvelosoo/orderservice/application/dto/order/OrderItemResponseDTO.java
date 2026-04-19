package io.github.gabrielvelosoo.orderservice.application.dto.order;

import java.math.BigDecimal;

public record OrderItemResponseDTO(
        Long id,
        Long productId,
        Integer quantity,
        BigDecimal price,
        Long orderId
    ) {
}
