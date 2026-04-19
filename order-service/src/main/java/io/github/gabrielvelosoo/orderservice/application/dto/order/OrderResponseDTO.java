package io.github.gabrielvelosoo.orderservice.application.dto.order;

import java.math.BigDecimal;
import java.util.List;

public record OrderResponseDTO(
        Long id,
        String status,
        BigDecimal total,
        List<OrderItemResponseDTO> items
    ) {
}
