package io.github.gabrielvelosoo.orderservice.application.mapper;

import io.github.gabrielvelosoo.orderservice.application.dto.order.OrderItemResponseDTO;
import io.github.gabrielvelosoo.orderservice.domain.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {

    @Mapping(target = "orderId", source = "order.id")
    OrderItemResponseDTO toDTO(OrderItem item);
}
