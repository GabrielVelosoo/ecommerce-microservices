package io.github.gabrielvelosoo.productservice.unit.application.validator.custom;

import io.github.gabrielvelosoo.productservice.application.validator.custom.CategoryValidator;
import io.github.gabrielvelosoo.productservice.domain.entity.Category;
import io.github.gabrielvelosoo.productservice.infrastructure.persistence.repository.CategoryRepository;
import io.github.gabrielvelosoo.productservice.infrastructure.exception.BusinessException;
import io.github.gabrielvelosoo.productservice.infrastructure.exception.DuplicateRecordException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryValidatorTest {

    @Mock
    CategoryRepository categoryRepository;

    @InjectMocks
    CategoryValidator categoryValidator;

    @Nested
    class ValidateOnCreateTests {

        @Test
        void shouldThrowExceptionWhenCategoryAlreadyExists() {
            Category category = new Category(1L, "Electronics");
            when(categoryRepository.findByNameAndParentCategory(
                    category.getName(),
                    category.getParentCategory()
            )).thenReturn(Optional.of(new Category(2L, "Electronics")));

            DuplicateRecordException e = assertThrows(
                    DuplicateRecordException.class,
                    () -> categoryValidator.validateOnCreate(category)
            );
            assertEquals("Category already exists", e.getMessage());
            verify(categoryRepository, times(1))
                    .findByNameAndParentCategory(category.getName(), category.getParentCategory());
        }

        @Test
        void shouldNotThrowExceptionWhenCategoryDoesNotExist() {
            Category category = new Category(null, "Books");
            when(categoryRepository.findByNameAndParentCategory(
                    category.getName(),
                    category.getParentCategory()
            )).thenReturn(Optional.empty());
            assertDoesNotThrow(() -> categoryValidator.validateOnCreate(category));
            verify(categoryRepository, times(1))
                    .findByNameAndParentCategory(category.getName(), category.getParentCategory());
        }

        @Test
        void shouldNotThrowExceptionWhenValidatingSameCategory() {
            Category category = new Category(1L, "Games");
            when(categoryRepository.findByNameAndParentCategory(
                    category.getName(),
                    category.getParentCategory()
            )).thenReturn(Optional.of(category));
            assertDoesNotThrow(() -> categoryValidator.validateOnCreate(category));
            verify(categoryRepository, times(1))
                    .findByNameAndParentCategory(category.getName(), category.getParentCategory());
        }

        @Test
        void shouldNotThrowExceptionWhenSameNameButDifferentParent() {
            Category parent1 = new Category(10L, "Parent 1");
            Category parent2 = new Category(20L, "Parent 2");
            Category category = new Category(null, "Books");
            category.setParentCategory(parent1);
            Category existing = new Category(5L, "Books");
            existing.setParentCategory(parent2);
            when(categoryRepository.findByNameAndParentCategory(
                    category.getName(),
                    category.getParentCategory()
            )).thenReturn(Optional.empty());
            assertDoesNotThrow(() -> categoryValidator.validateOnCreate(category));
        }
    }

    @Nested
    class ValidateOnDeleteTests {

        @Test
        void shouldThrowExceptionWhenCategoryHasSubcategories() {
            Category category = new Category(1L, "Root");
            category.getSubcategories().add(new Category(2L, "Child"));
            BusinessException e = assertThrows(
                    BusinessException.class,
                    () -> categoryValidator.validateOnDelete(category)
            );
            assertEquals("Cannot delete a category that has subcategories", e.getMessage());
        }

        @Test
        void shouldNotThrowExceptionWhenCategoryHasNoSubcategories() {
            Category category = new Category(1L, "Leaf");
            assertDoesNotThrow(() -> categoryValidator.validateOnDelete(category));
        }
    }
}