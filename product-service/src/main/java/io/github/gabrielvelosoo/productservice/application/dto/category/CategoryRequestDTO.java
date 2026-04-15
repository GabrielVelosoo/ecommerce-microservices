package io.github.gabrielvelosoo.productservice.application.dto.category;

import io.github.gabrielvelosoo.productservice.application.validator.group.ValidateNotBlank;
import jakarta.validation.constraints.NotBlank;

public record CategoryRequestDTO(
        @NotBlank(message = "Required field", groups = ValidateNotBlank.class)
        String name,
        Long parentCategoryId
    ) {
}
