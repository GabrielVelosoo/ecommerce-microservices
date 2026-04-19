package io.github.gabrielvelosoo.cartservice.application.dto.cart;

public record CartItemResponseDTO(
        Long productId,
        Integer quantity
    ) {
}
