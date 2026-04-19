package io.github.gabrielvelosoo.cartservice.application.dto.checkout;

import java.util.List;

public record CheckoutRequestDTO(
        String userId,
        List<CheckoutItemDTO> items
    ) {
}
