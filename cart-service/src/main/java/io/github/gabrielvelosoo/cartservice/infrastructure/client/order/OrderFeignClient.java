package io.github.gabrielvelosoo.cartservice.infrastructure.client.order;

import io.github.gabrielvelosoo.cartservice.application.dto.checkout.CheckoutRequestDTO;
import io.github.gabrielvelosoo.cartservice.application.dto.checkout.CheckoutResponseDTO;
import io.github.gabrielvelosoo.cartservice.infrastructure.configuration.feign.OrderFeignConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "order-service",
        url = "${services.order.url}",
        configuration = OrderFeignConfiguration.class
)
public interface OrderFeignClient {

    @PostMapping("/api/v1/internal/orders/from-cart")
    CheckoutResponseDTO createOrder(@RequestBody CheckoutRequestDTO request);
}
