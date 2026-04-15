package io.github.gabrielvelosoo.productservice.domain.service;

import io.github.gabrielvelosoo.productservice.domain.entity.Category;

import java.util.List;

public interface CategoryService {

    Category save(Category category);
    Category findById(Long id);
    List<Category> getParentlessCategories();
    void delete(Category category);
}
