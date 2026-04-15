package io.github.gabrielvelosoo.productservice.application.validator.custom;

import io.github.gabrielvelosoo.productservice.domain.entity.Category;
import io.github.gabrielvelosoo.productservice.infrastructure.persistence.repository.CategoryRepository;
import io.github.gabrielvelosoo.productservice.infrastructure.exception.BusinessException;
import io.github.gabrielvelosoo.productservice.infrastructure.exception.DuplicateRecordException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CategoryValidator {

    private static final Logger logger = LogManager.getLogger(CategoryValidator.class);

    private final CategoryRepository categoryRepository;

    public CategoryValidator(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public void validateOnCreate(Category category) {
        logger.debug("Validating category creation. Name='{}'", category.getName());
        if(categoryExists(category)) {
            logger.warn("The category='{}' already exists. Cannot create a duplicate.", category.getName());
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
        logger.debug("Validating if category='{}' (id='{}') can be deleted", category.getName(), category.getId());
        if(hasSubcategories(category)) {
            logger.warn("The category='{}' id='{}' cannot be deleted because it has subcategories",
                    category.getName(),
                    category.getId()
            );
            throw new BusinessException("Cannot delete a category that has subcategories");
        }
    }

    private boolean hasSubcategories(Category category) {
        return category.getSubcategories() != null && !category.getSubcategories().isEmpty();
    }
}
