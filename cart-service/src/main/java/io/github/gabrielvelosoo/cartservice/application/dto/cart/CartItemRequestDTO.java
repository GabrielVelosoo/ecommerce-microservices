package io.github.gabrielvelosoo.cartservice.application.dto.cart;

import io.github.gabrielvelosoo.cartservice.application.validator.group.ValidateNotNull;
import io.github.gabrielvelosoo.cartservice.application.validator.group.ValidateOthers;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CartItemRequestDTO(
        @NotNull(message = "Required field", groups = ValidateNotNull.class)
        Long productId,

        @NotNull(message = "Required field", groups = ValidateNotNull.class)
        @Positive(message = "Field must be greater than 0", groups = ValidateOthers.class)
        Integer quantity
    ) {
}
