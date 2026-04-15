package io.github.gabrielvelosoo.productservice.unit.application.validator.custom;

import io.github.gabrielvelosoo.productservice.application.validator.custom.ProductValidator;
import io.github.gabrielvelosoo.productservice.domain.entity.Category;
import io.github.gabrielvelosoo.productservice.domain.entity.Product;
import io.github.gabrielvelosoo.productservice.infrastructure.persistence.repository.CategoryRepository;
import io.github.gabrielvelosoo.productservice.infrastructure.exception.RecordNotFoundException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductValidatorTest {

    @Mock
    CategoryRepository categoryRepository;

    @InjectMocks
    ProductValidator productValidator;

    @Nested
    class ValidateOnCreateAndUpdateTests {

        @Test
        void shouldNotThrowExceptionWhenCategoryExists() {
            Category category = new Category(1L, "Electronics");
            Product product = new Product();
            product.setCategory(category);
            when(categoryRepository.existsById(category.getId())).thenReturn(true);
            assertDoesNotThrow(() -> productValidator.validateOnCreateAndUpdate(product));
            verify(categoryRepository, times(1)).existsById(category.getId());
        }

        @Test
        void shouldThrowExceptionWhenCategoryDoesNotExist() {
            Category category = new Category(1L, "Electronics");
            Product product = new Product();
            product.setCategory(category);
            when(categoryRepository.existsById(category.getId())).thenReturn(false);
            RecordNotFoundException e = assertThrows(
                    RecordNotFoundException.class,
                    () -> productValidator.validateOnCreateAndUpdate(product)
            );
            assertEquals("Category not found", e.getMessage());
            verify(categoryRepository, times(1)).existsById(category.getId());
        }
    }
}