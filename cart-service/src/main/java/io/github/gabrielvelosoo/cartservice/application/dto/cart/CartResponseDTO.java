package io.github.gabrielvelosoo.cartservice.application.dto.cart;

import java.util.Map;

public record CartResponseDTO(
        String userId,
        Map<Long, CartItemResponseDTO> items
    ) {
}
