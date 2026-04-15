package io.github.gabrielvelosoo.productservice.infrastructure.service;

import io.github.gabrielvelosoo.productservice.domain.entity.Category;
import io.github.gabrielvelosoo.productservice.infrastructure.persistence.repository.CategoryRepository;
import io.github.gabrielvelosoo.productservice.domain.service.CategoryService;
import io.github.gabrielvelosoo.productservice.infrastructure.exception.RecordNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    private static final Logger logger = LogManager.getLogger(CategoryServiceImpl.class);

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public Category save(Category category) {
        return categoryRepository.save(category);
    }

    @Override
    public Category findById(Long id) {
        logger.debug("Searching for category id='{}'", id);
        return categoryRepository.findById(id)
                .orElseThrow( () -> {
                    logger.warn("Category not found id='{}'", id);
                    return new RecordNotFoundException("Category not found");
                } );
    }

    @Override
    public List<Category> getParentlessCategories() {
        logger.debug("Searching for categories where parentCategory is null");
        List<Category> result = categoryRepository.findByParentCategoryIsNull();
        logger.debug("Loading subcategories");
        result.forEach(this::loadSubcategories);
        return result;
    }

    private void loadSubcategories(Category category) {
        category.getSubcategories().forEach(this::loadSubcategories);
    }

    @Override
    public void delete(Category category) {
        categoryRepository.delete(category);
    }
}
