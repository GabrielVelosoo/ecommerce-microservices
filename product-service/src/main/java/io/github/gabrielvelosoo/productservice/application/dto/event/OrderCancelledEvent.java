package io.github.gabrielvelosoo.productservice.application.dto.event;

import java.util.List;

public record OrderCancelledEvent(
        Long orderId,
        List<OrderItemEventDTO> items
    ) {
}
