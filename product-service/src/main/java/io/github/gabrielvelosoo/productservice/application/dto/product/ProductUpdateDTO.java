package io.github.gabrielvelosoo.productservice.application.dto.product;

import io.github.gabrielvelosoo.productservice.application.validator.group.ValidateOthers;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

public record ProductUpdateDTO(
        @Size(min = 2, max = 100, message = "Field must be between 2 and 100 characters", groups = ValidateOthers.class)
        String name,

        @Size(min = 5, message = "Field must be at least 5 characters long", groups = ValidateOthers.class)
        String description,

        @Positive(message = "Field must be greater than 0", groups = ValidateOthers.class)
        BigDecimal price,

        @Positive(message = "Field must be greater than 0", groups = ValidateOthers.class)
        Integer stockQuantity,

        MultipartFile image, // imagem opcional

        Long categoryId
    ) {
}
