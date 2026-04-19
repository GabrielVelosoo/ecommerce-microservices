package io.github.gabrielvelosoo.productservice.application.mapper;

import io.github.gabrielvelosoo.productservice.application.dto.category.CategoryRequestDTO;
import io.github.gabrielvelosoo.productservice.application.dto.category.CategoryResponseDTO;
import io.github.gabrielvelosoo.productservice.domain.entity.Category;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    Category toEntity(CategoryRequestDTO request);
    CategoryResponseDTO toDTO(Category category);
    List<CategoryResponseDTO> toDTOs(List<Category> categories);
}
