package io.github.gabrielvelosoo.productservice.unit.infrastructure.service;

import io.github.gabrielvelosoo.productservice.domain.entity.Category;
import io.github.gabrielvelosoo.productservice.infrastructure.persistence.repository.CategoryRepository;
import io.github.gabrielvelosoo.productservice.infrastructure.exception.RecordNotFoundException;
import io.github.gabrielvelosoo.productservice.infrastructure.service.CategoryServiceImpl;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    CategoryRepository categoryRepository;

    @InjectMocks
    CategoryServiceImpl categoryService;

    @Nested
    class SaveTests {

        @Test
        void shouldGenerateSlugAndSaveCategorySuccessfully() {
            Category category = new Category(1L, "Books");
            when(categoryRepository.save(category))
                    .thenAnswer(invocation -> invocation.getArgument(0));
            Category savedCategory = categoryService.save(category);
            assertNotNull(savedCategory);
            assertSame(category, savedCategory);
            assertEquals("books", savedCategory.getSlug());
            verify(categoryRepository, times(1)).save(category);
        }
    }

    @Nested
    class FindByIdTests {

        private static final Long CATEGORY_ID = 1L;

        @Test
        void shouldFindCategoryByIdSuccessfully() {
            Category category = new Category(1L, "Electronics");
            when(categoryRepository.findById(CATEGORY_ID)).thenReturn(Optional.of(category));
            Category foundCategory = categoryService.findById(CATEGORY_ID);
            assertNotNull(foundCategory);
            assertEquals(CATEGORY_ID, foundCategory.getId());
            verify(categoryRepository, times(1)).findById(CATEGORY_ID);
        }

        @Test
        void shouldThrowExceptionWhenCategoryDoesNotExist() {
            when(categoryRepository.findById(CATEGORY_ID)).thenReturn(Optional.empty());
            RecordNotFoundException e = assertThrows(
                    RecordNotFoundException.class,
                    () -> categoryService.findById(CATEGORY_ID)
            );
            assertEquals("Category not found", e.getMessage());
            verify(categoryRepository, times(1)).findById(CATEGORY_ID);
        }
    }

    @Nested
    class GetParentlessCategoriesTests {

        @Test
        void shouldReturnRootCategoriesWithLoadedSubcategories() {
            Category child1 = new Category(2L, "Child 1");
            Category child2 = new Category(3L, "Child 2");
            Category root = new Category(1L, "Root");
            root.getSubcategories().add(child1);
            root.getSubcategories().add(child2);
            when(categoryRepository.findByParentCategoryIsNull()).thenReturn(List.of(root));
            List<Category> result = categoryService.getParentlessCategories();
            assertNotNull(result);
            assertEquals(1, result.size());
            Category returnedRoot = result.getFirst();
            assertEquals(2, returnedRoot.getSubcategories().size());
            verify(categoryRepository, times(1)).findByParentCategoryIsNull();
        }
    }

    @Nested
    class DeleteTests {

        @Test
        void shouldDeleteCategorySuccessfully() {
            Category category = new Category(1L, "Games");
            categoryService.delete(category);
            verify(categoryRepository, times(1)).delete(category);
        }
    }
}