package io.github.gabrielvelosoo.cartservice.application.dto.checkout;

import java.math.BigDecimal;

public record CheckoutResponseDTO(
        Long id,
        String status,
        BigDecimal total
    ) {
}
