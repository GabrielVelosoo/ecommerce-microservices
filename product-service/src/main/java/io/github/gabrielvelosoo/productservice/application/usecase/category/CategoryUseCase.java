package io.github.gabrielvelosoo.productservice.application.usecase.category;

import io.github.gabrielvelosoo.productservice.application.dto.category.CategoryRequestDTO;
import io.github.gabrielvelosoo.productservice.application.dto.category.CategoryResponseDTO;

import java.util.List;

public interface CategoryUseCase {

    CategoryResponseDTO create(CategoryRequestDTO categoryRequestDTO);
    List<CategoryResponseDTO> getRootCategories();
    void delete(Long categoryId);
}
