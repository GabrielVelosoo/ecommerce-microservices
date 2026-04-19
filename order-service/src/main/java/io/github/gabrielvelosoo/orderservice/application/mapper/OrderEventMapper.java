package io.github.gabrielvelosoo.orderservice.application.mapper;

import io.github.gabrielvelosoo.orderservice.application.dto.event.OrderItemEventDTO;
import io.github.gabrielvelosoo.orderservice.domain.entity.OrderItem;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderEventMapper {

    public List<OrderItemEventDTO> toEventItems(List<OrderItem> items) {
        return items.stream()
                .map(item -> new OrderItemEventDTO(
                        item.getProductId(),
                        item.getQuantity()
                ))
                .toList();
    }
}
