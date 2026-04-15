package io.github.gabrielvelosoo.productservice.integration.infrastructure.persistence.repository;

import io.github.gabrielvelosoo.productservice.domain.entity.Category;
import io.github.gabrielvelosoo.productservice.infrastructure.persistence.repository.CategoryRepository;
import io.github.gabrielvelosoo.productservice.integration.configuration.AbstractIT;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CategoryRepositoryIT extends AbstractIT {

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    EntityManager entityManager;

    @AfterEach
    void clear() {
        entityManager.clear();
    }

    @Nested
    class FindByNameAndParentCategoryTests  {

        @Test
        void shouldFindCategoryByNameAndParent() {
            Category parent = persistCategory("Parent", null);
            Category child = persistCategory("Child", parent);
            Optional<Category> result = categoryRepository.findByNameAndParentCategory(child.getName(), parent);
            assertThat(result).isPresent();
            assertThat(result.get().getName()).isEqualTo(child.getName());
            assertThat(result.get().getParentCategory()).isEqualTo(parent);
        }

        @Test
        void shouldReturnEmptyWhenCategoryNotFound() {
            Category parent = persistCategory("Parent", null);
            assertThat(categoryRepository.findByNameAndParentCategory("NonExistent", parent)).isEmpty();
        }
    }

    @Nested
    class FindByParentCategoryIsNullTests {

        @Test
        void shouldFindRootCategories() {
            Category root1 = persistCategory("Root1", null);
            Category root2 = persistCategory("Root2", null);
            persistCategory("Child1", root1);
            assertThat(categoryRepository.findByParentCategoryIsNull())
                    .hasSize(2).containsExactlyInAnyOrder(root1, root2);
        }

        @Test
        void shouldReturnEmptyWhenNoRootCategoriesExist() {
            assertThat(categoryRepository.findByParentCategoryIsNull()).isEmpty();
        }
    }

    private Category persistCategory(String name, Category parent) {
        Category category = new Category();
        category.setName(name);
        category.setParentCategory(parent);
        category.setSlug(category.generateSlug(name));
        entityManager.persist(category);
        entityManager.flush();
        return category;
    }
}
