package io.github.gabrielvelosoo.cartservice.application.mapper;

import io.github.gabrielvelosoo.cartservice.application.dto.cart.CartResponseDTO;
import io.github.gabrielvelosoo.cartservice.domain.entity.Cart;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CartMapper {

    CartResponseDTO toDTO(Cart cart);
}
