package io.github.gabrielvelosoo.cartservice.application.gateway;

import io.github.gabrielvelosoo.cartservice.application.dto.checkout.CheckoutRequestDTO;
import io.github.gabrielvelosoo.cartservice.application.dto.checkout.CheckoutResponseDTO;

public interface OrderGateway {

    CheckoutResponseDTO createOrder(CheckoutRequestDTO request);
}
