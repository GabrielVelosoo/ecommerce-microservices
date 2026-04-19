package io.github.gabrielvelosoo.productservice.application.validator.custom;

import io.github.gabrielvelosoo.productservice.domain.entity.Category;
import io.github.gabrielvelosoo.productservice.infrastructure.persistence.repository.CategoryRepository;
import io.github.gabrielvelosoo.productservice.application.exception.BusinessException;
import io.github.gabrielvelosoo.productservice.application.exception.DuplicateRecordException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CategoryValidator {

    private final CategoryRepository categoryRepository;

    public CategoryValidator(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public void validateOnCreate(Category category) {
        if(categoryExists(category)) {
            throw new DuplicateRecordException("Category already exists");
        }
    }

    private boolean categoryExists(Category category) {
        Optional<Category> optionalCategory = categoryRepository
                .findByNameAndParentCategory(category.getName(), category.getParentCategory());
        if(category.getId() == null) {
            return optionalCategory.isPresent();
        }
        return optionalCategory
                .map(c -> !c.getId().equals(category.getId()))
                .orElse(false);
    }

    public void validateOnDelete(Category category) {
        if(hasSubcategories(category)) {
            throw new BusinessException("Cannot delete a category that has subcategories");
        }
    }

    private boolean hasSubcategories(Category category) {
        return category.getSubcategories() != null && !category.getSubcategories().isEmpty();
    }
}
