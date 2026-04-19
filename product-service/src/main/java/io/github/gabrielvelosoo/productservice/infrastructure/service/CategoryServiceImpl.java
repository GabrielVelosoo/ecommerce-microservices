package io.github.gabrielvelosoo.productservice.infrastructure.service;

import io.github.gabrielvelosoo.productservice.domain.entity.Category;
import io.github.gabrielvelosoo.productservice.infrastructure.persistence.repository.CategoryRepository;
import io.github.gabrielvelosoo.productservice.domain.service.CategoryService;
import io.github.gabrielvelosoo.productservice.application.exception.RecordNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

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
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Category not found"));
    }

    @Override
    public List<Category> getParentlessCategories() {
        List<Category> result = categoryRepository.findByParentCategoryIsNull();
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
