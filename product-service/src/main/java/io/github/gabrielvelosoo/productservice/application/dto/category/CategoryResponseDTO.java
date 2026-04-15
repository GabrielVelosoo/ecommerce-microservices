package io.github.gabrielvelosoo.productservice.application.dto.category;

import java.util.List;

public record CategoryResponseDTO(
        Long id,
        String name,
        String slug,
        List<CategoryResponseDTO> subcategories
    ) {
}
