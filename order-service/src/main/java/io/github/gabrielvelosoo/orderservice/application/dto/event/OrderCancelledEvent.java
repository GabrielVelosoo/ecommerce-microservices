package io.github.gabrielvelosoo.orderservice.application.dto.event;

import java.util.List;

public record OrderCancelledEvent(
        Long orderId,
        List<OrderItemEventDTO> items
    ) {
}
