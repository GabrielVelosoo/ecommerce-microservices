package io.github.gabrielvelosoo.productservice.application.mapper;

import io.github.gabrielvelosoo.productservice.application.dto.product.ProductCreateDTO;
import io.github.gabrielvelosoo.productservice.application.dto.product.ProductResponseDTO;
import io.github.gabrielvelosoo.productservice.application.dto.product.ProductUpdateDTO;
import io.github.gabrielvelosoo.productservice.domain.entity.Category;
import io.github.gabrielvelosoo.productservice.domain.entity.Product;
import io.github.gabrielvelosoo.productservice.domain.service.CategoryService;
import io.github.gabrielvelosoo.productservice.domain.service.ImageStorageService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

@Mapper(componentModel = "spring", uses = CategoryMapper.class)
public abstract class ProductMapper {

    @Autowired
    CategoryService categoryService;

    @Autowired
    ImageStorageService imageStorageService;

    @Mapping(target = "category", expression = "java( categoryService.findById(createDTO.categoryId()) )")
    @Mapping(target = "imageUrl", ignore = true)
    public abstract Product toEntity(ProductCreateDTO createDTO);

    @Mapping(target = "categoryPath", expression = "java( buildCategoryPath(product.getCategory()) )")
    public abstract ProductResponseDTO toDTO(Product product);

    public void update(Product product, ProductUpdateDTO updateDTO) throws IOException {
        if(updateDTO.name() != null) product.setName(updateDTO.name());
        if(updateDTO.description() != null) product.setDescription(updateDTO.description());
        if(updateDTO.price() != null) product.setPrice(updateDTO.price());
        if(updateDTO.stockQuantity() != null) product.setStockQuantity(updateDTO.stockQuantity());
        if(updateDTO.categoryId() != null) {
            Category category = categoryService.findById(updateDTO.categoryId());
            product.setCategory(category);
        }
        if(updateDTO.image() != null && !updateDTO.image().isEmpty()) {
            String newImageUrl = imageStorageService.replace(product.getImageUrl(), updateDTO.image());
            product.setImageUrl(newImageUrl);
        }
    }

    public String buildCategoryPath(Category category) {
        if(category == null) return "";
        if(category.getParentCategory() != null) {
            return category.getParentCategory().getName() + " > " + category.getName();
        }
        return category.getName();
    }
}
