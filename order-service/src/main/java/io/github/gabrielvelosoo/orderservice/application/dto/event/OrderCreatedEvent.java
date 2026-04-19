package io.github.gabrielvelosoo.orderservice.application.dto.event;

import java.util.List;

public record OrderCreatedEvent(
        Long orderId,
        List<OrderItemEventDTO> items
    ) {
}
