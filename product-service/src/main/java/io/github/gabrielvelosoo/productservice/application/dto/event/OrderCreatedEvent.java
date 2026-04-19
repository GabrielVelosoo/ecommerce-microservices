package io.github.gabrielvelosoo.productservice.application.dto.event;

import java.util.List;

public record OrderCreatedEvent(
        Long orderId,
        List<OrderItemEventDTO> items
    ) {
}
