package io.github.gabrielvelosoo.orderservice.application.dto.order;

import io.github.gabrielvelosoo.orderservice.application.validator.group.ValidateNotNull;
import io.github.gabrielvelosoo.orderservice.application.validator.group.ValidateOthers;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CreateOrderItemDTO(
        @NotNull(message = "Product ID is required", groups = ValidateNotNull.class)
        Long productId,

        @NotNull(message = "Quantity is required", groups = ValidateNotNull.class)
        @Positive(message = "Quantity must be greater than 0", groups = ValidateOthers.class)
        Integer quantity,

        @NotNull(message = "Price is required", groups = ValidateNotNull.class)
        @Positive(message = "Price must be greater than 0", groups = ValidateOthers.class)
        BigDecimal price
    ) {
}
