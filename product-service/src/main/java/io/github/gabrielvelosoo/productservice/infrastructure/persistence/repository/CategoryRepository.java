package io.github.gabrielvelosoo.productservice.infrastructure.persistence.repository;

import io.github.gabrielvelosoo.productservice.domain.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByNameAndParentCategory(String name, Category parentCategory);
    List<Category> findByParentCategoryIsNull();
}
