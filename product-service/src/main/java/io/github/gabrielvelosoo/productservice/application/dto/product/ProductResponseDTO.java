package io.github.gabrielvelosoo.productservice.application.dto.product;

import io.github.gabrielvelosoo.productservice.application.dto.category.CategoryResponseDTO;

import java.math.BigDecimal;

public record ProductResponseDTO(
        Long id,
        String name,
        String description,
        BigDecimal price,
        Integer stockQuantity,
        String imageUrl,
        CategoryResponseDTO category,
        String categoryPath
    ) {
}
