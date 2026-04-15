package io.github.gabrielvelosoo.productservice.application.mapper;

import io.github.gabrielvelosoo.productservice.application.dto.product.ProductCreateDTO;
import io.github.gabrielvelosoo.productservice.application.dto.product.ProductResponseDTO;
import io.github.gabrielvelosoo.productservice.application.dto.product.ProductUpdateDTO;
import io.github.gabrielvelosoo.productservice.domain.entity.Category;
import io.github.gabrielvelosoo.productservice.domain.entity.Product;
import io.github.gabrielvelosoo.productservice.domain.service.CategoryService;
import io.github.gabrielvelosoo.productservice.domain.service.ImageStorageService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

@Mapper(componentModel = "spring", uses = CategoryMapper.class)
public abstract class ProductMapper {

    private static final Logger logger = LogManager.getLogger(ProductMapper.class);

    @Autowired
    CategoryService categoryService;

    @Autowired
    ImageStorageService imageStorageService;

    @Mapping(target = "category", expression = "java( categoryService.findById(productCreateDTO.categoryId()) )")
    @Mapping(target = "imageUrl", ignore = true)
    public abstract Product toEntity(ProductCreateDTO productCreateDTO);

    @Mapping(target = "categoryPath", expression = "java( buildCategoryPath(product.getCategory()) )")
    public abstract ProductResponseDTO toDTO(Product product);

    public void update(Product product, ProductUpdateDTO dto) throws IOException {
        logger.debug("Updating product entity fields for id='{}'", product.getId());
        if(dto.name() != null) product.setName(dto.name());
        if(dto.description() != null) product.setDescription(dto.description());
        if(dto.price() != null) product.setPrice(dto.price());
        if(dto.stockQuantity() != null) product.setStockQuantity(dto.stockQuantity());
        if(dto.categoryId() != null) {
            logger.debug("Updating category for product id='{}'", product.getId());
            Category category = categoryService.findById(dto.categoryId());
            product.setCategory(category);
        }
        if(dto.image() != null && !dto.image().isEmpty()) {
            logger.debug("Replacing image for product id='{}'", product.getId());
            String newImageUrl = imageStorageService.replace(product.getImageUrl(), dto.image());
            product.setImageUrl(newImageUrl);
            logger.debug("New image saved for product id='{}': '{}'", product.getId(), newImageUrl);
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
