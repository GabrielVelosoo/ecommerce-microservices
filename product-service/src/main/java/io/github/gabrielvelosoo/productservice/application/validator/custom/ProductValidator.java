package io.github.gabrielvelosoo.productservice.application.validator.custom;

import io.github.gabrielvelosoo.productservice.domain.entity.Product;
import io.github.gabrielvelosoo.productservice.infrastructure.persistence.repository.CategoryRepository;
import io.github.gabrielvelosoo.productservice.application.exception.RecordNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class ProductValidator {

    private final CategoryRepository categoryRepository;

    public ProductValidator(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public void validateOnCreateAndUpdate(Product product) {
        if(!categoryExists(product)) {
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
