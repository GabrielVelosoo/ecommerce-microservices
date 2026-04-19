package io.github.gabrielvelosoo.cartservice.application.usecase;

import io.github.gabrielvelosoo.cartservice.application.dto.cart.CartItemRequestDTO;
import io.github.gabrielvelosoo.cartservice.application.dto.cart.CartResponseDTO;

public interface CartUseCase {

    CartResponseDTO getCart();
    CartResponseDTO addItem(CartItemRequestDTO request);
    CartResponseDTO updateItem(CartItemRequestDTO request);
    CartResponseDTO removeItem(Long productId);
}
