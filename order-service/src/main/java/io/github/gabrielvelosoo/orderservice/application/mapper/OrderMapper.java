package io.github.gabrielvelosoo.orderservice.application.mapper;

import io.github.gabrielvelosoo.orderservice.application.dto.order.OrderResponseDTO;
import io.github.gabrielvelosoo.orderservice.domain.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = OrderItemMapper.class)
public interface OrderMapper {

    @Mapping(target = "status", source = "status")
    OrderResponseDTO toDTO(Order order);
}
