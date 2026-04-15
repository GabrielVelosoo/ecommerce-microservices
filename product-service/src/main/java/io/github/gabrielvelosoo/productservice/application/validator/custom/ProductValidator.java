package io.github.gabrielvelosoo.productservice.application.validator.custom;

import io.github.gabrielvelosoo.productservice.domain.entity.Product;
import io.github.gabrielvelosoo.productservice.infrastructure.persistence.repository.CategoryRepository;
import io.github.gabrielvelosoo.productservice.infrastructure.exception.RecordNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class ProductValidator {

    private static final Logger logger = LogManager.getLogger(ProductValidator.class);

    private final CategoryRepository categoryRepository;

    public ProductValidator(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public void validateOnCreateAndUpdate(Product product) {
        Long categoryId = product.getCategory() == null ? null : product.getCategory().getId();
        logger.debug("Validating product creation or update. Category ID='{}'", categoryId);
        logger.debug("Validating category existence for product creation or update.");
        if(!categoryExists(product)) {
            logger.warn("Category not found. Cannot create/update product.");
            throw new RecordNotFoundException("Category not found");
        }
    }

    private boolean categoryExists(Product product) {
        if(product.getCategory() == null || product.getCategory().getId() == null) {
            return false;
        }
        return categoryRepository.existsById(product.getCategory().getId());
    }
}
