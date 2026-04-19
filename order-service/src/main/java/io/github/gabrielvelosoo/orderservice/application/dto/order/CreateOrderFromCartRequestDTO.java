package io.github.gabrielvelosoo.orderservice.application.dto.order;

import io.github.gabrielvelosoo.orderservice.application.validator.group.ValidateNotNull;
import io.github.gabrielvelosoo.orderservice.application.validator.group.ValidateOthers;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CreateOrderFromCartRequestDTO(
        @NotBlank(message = "User ID is required", groups = ValidateNotNull.class)
        String userId,

        @NotNull(message = "Items is required", groups = ValidateNotNull.class)
        @NotEmpty(message = "It is not possible to create an order with an empty cart", groups = ValidateOthers.class)
        List<CreateOrderItemDTO> items
    ) {
}
