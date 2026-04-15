package io.github.gabrielvelosoo.productservice.application.dto.product;

import io.github.gabrielvelosoo.productservice.application.validator.group.ValidateNotBlank;
import io.github.gabrielvelosoo.productservice.application.validator.group.ValidateOthers;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

public record ProductCreateDTO(
        @NotBlank(message = "Required field", groups = ValidateNotBlank.class)
        @Size(min = 2, max = 100, message = "Field must be have between 2 and 100 characters", groups = ValidateOthers.class)
        String name,

        @NotBlank(message = "Required field", groups = ValidateNotBlank.class)
        @Size(min = 5, message = "Field must be at least 5 characters long", groups = ValidateOthers.class)
        String description,

        @NotNull(message = "Required field", groups = ValidateNotBlank.class)
        @Positive(message = "Field must be greater than 0", groups = ValidateOthers.class)
        BigDecimal price,

        @NotNull(message = "Required field", groups = ValidateNotBlank.class)
        @Positive(message = "Field must be greater than 0", groups = ValidateOthers.class)
        Integer stockQuantity,

        @NotNull(message = "Required field", groups = ValidateNotBlank.class)
        MultipartFile image,

        @NotNull(message = "Required field", groups = ValidateNotBlank.class)
        Long categoryId
    ) {
}
