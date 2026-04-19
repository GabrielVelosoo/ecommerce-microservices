package io.github.gabrielvelosoo.cartservice.infrastructure.client.order;

import io.github.gabrielvelosoo.cartservice.application.dto.checkout.CheckoutRequestDTO;
import io.github.gabrielvelosoo.cartservice.application.dto.checkout.CheckoutResponseDTO;
import io.github.gabrielvelosoo.cartservice.application.gateway.OrderGateway;
import org.springframework.stereotype.Component;

@Component
public class OrderGatewayImpl implements OrderGateway {

    private final OrderFeignClient orderFeignClient;

    public OrderGatewayImpl(OrderFeignClient orderFeignClient) {
        this.orderFeignClient = orderFeignClient;
    }

    @Override
    public CheckoutResponseDTO createOrder(CheckoutRequestDTO request) {
        return orderFeignClient.createOrder(request);
    }
}
