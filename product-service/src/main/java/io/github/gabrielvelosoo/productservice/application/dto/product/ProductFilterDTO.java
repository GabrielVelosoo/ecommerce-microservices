package io.github.gabrielvelosoo.productservice.application.dto.product;

import jakarta.annotation.Nullable;

import java.math.BigDecimal;

public record ProductFilterDTO(
        @Nullable
        String name,

        @Nullable
        BigDecimal minPrice,

        @Nullable
        BigDecimal maxPrice,

        @Nullable
        Integer minStock,

        @Nullable
        Integer maxStock,

        @Nullable
        Long categoryId
    ) {
}
