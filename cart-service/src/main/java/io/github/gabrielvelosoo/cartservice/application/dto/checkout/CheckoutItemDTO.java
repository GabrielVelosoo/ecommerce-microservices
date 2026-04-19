package io.github.gabrielvelosoo.cartservice.application.dto.checkout;

import java.math.BigDecimal;

public record CheckoutItemDTO(
        Long productId,
        Integer quantity,
        BigDecimal price
    ) {
}
