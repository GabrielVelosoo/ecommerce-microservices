package io.github.gabrielvelosoo.productservice.unit.application.usecase.category;

import io.github.gabrielvelosoo.productservice.application.dto.category.CategoryRequestDTO;
import io.github.gabrielvelosoo.productservice.application.dto.category.CategoryResponseDTO;
import io.github.gabrielvelosoo.productservice.application.mapper.CategoryMapper;
import io.github.gabrielvelosoo.productservice.application.usecase.category.CategoryUseCaseImpl;
import io.github.gabrielvelosoo.productservice.application.validator.custom.CategoryValidator;
import io.github.gabrielvelosoo.productservice.domain.entity.Category;
import io.github.gabrielvelosoo.productservice.domain.service.CategoryService;
import io.github.gabrielvelosoo.productservice.infrastructure.exception.BusinessException;
import io.github.gabrielvelosoo.productservice.infrastructure.exception.RecordNotFoundException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryUseCaseImplTest {

    @Mock
    CategoryService categoryService;

    @Mock
    CategoryMapper categoryMapper;

    @Mock
    CategoryValidator categoryValidator;

    @InjectMocks
    CategoryUseCaseImpl categoryUseCase;

    @Nested
    class CreateTests {

        @Test
        void shouldCreateCategoryWithoutParentSuccessfully() {
            CategoryRequestDTO requestDTO = new CategoryRequestDTO("Books", null);
            Category category = new Category(null, "Books");
            Category savedCategory = new Category(1L, "Books");
            CategoryResponseDTO responseDTO = new CategoryResponseDTO(
                    1L, "Books", "books", List.of()
            );
            when(categoryMapper.toEntity(requestDTO)).thenReturn(category);
            when(categoryService.save(category)).thenReturn(savedCategory);
            when(categoryMapper.toDTO(savedCategory)).thenReturn(responseDTO);
            CategoryResponseDTO result = categoryUseCase.create(requestDTO);
            assertNotNull(result);
            assertEquals(1L, result.id());
            verify(categoryMapper, times(1)).toEntity(requestDTO);
            verify(categoryValidator, times(1)).validateOnCreate(category);
            verify(categoryService, times(1)).save(category);
            verify(categoryMapper, times(1)).toDTO(savedCategory);
            verify(categoryService, never()).findById(any());
        }

        @Test
        void shouldCreateCategoryWithParentSuccessfully() {
            Long parentId = 10L;
            CategoryRequestDTO requestDTO = new CategoryRequestDTO("Laptops", parentId);
            Category category = new Category(null, "Laptops");
            Category parent = new Category(parentId, "Electronics");
            Category savedCategory = new Category(2L, "Laptops");
            savedCategory.setParentCategory(parent);
            CategoryResponseDTO responseDTO = new CategoryResponseDTO(
                    2L, "Laptops", "laptops", List.of()
            );
            when(categoryMapper.toEntity(requestDTO)).thenReturn(category);
            when(categoryService.findById(parentId)).thenReturn(parent);
            when(categoryService.save(category)).thenReturn(savedCategory);
            when(categoryMapper.toDTO(savedCategory)).thenReturn(responseDTO);
            CategoryResponseDTO result = categoryUseCase.create(requestDTO);
            assertNotNull(result);
            assertEquals(2L, result.id());
            verify(categoryService, times(1)).findById(parentId);
            verify(categoryValidator, times(1)).validateOnCreate(category);
            verify(categoryService, times(1)).save(category);
        }

        @Test
        void shouldPropagateExceptionWhenParentDoesNotExist() {
            Long parentId = 99L;
            CategoryRequestDTO requestDTO = new CategoryRequestDTO("Tablets", parentId);
            Category category = new Category(null, "Tablets");
            when(categoryMapper.toEntity(requestDTO)).thenReturn(category);
            when(categoryService.findById(parentId))
                    .thenThrow(new RecordNotFoundException("Category not found"));
            RecordNotFoundException e = assertThrows(
                    RecordNotFoundException.class,
                    () -> categoryUseCase.create(requestDTO)
            );
            assertEquals("Category not found", e.getMessage());
            verify(categoryService, times(1)).findById(parentId);
            verify(categoryService, never()).save(any());
        }
    }

    @Nested
    class GetRootCategoriesTests {

        @Test
        void shouldReturnRootCategoriesMappedToDTO() {
            Category root = new Category(1L, "Root");
            List<Category> categories = List.of(root);
            CategoryResponseDTO responseDTO = new CategoryResponseDTO(
                    1L, "Root", "root", List.of()
            );
            when(categoryService.getParentlessCategories()).thenReturn(categories);
            when(categoryMapper.toDTOs(categories)).thenReturn(List.of(responseDTO));
            List<CategoryResponseDTO> result = categoryUseCase.getRootCategories();
            assertEquals(1, result.size());
            assertEquals("Root", result.getFirst().name());
            verify(categoryService, times(1)).getParentlessCategories();
            verify(categoryMapper, times(1)).toDTOs(categories);
        }
    }

    @Nested
    class DeleteTests {

        private static final Long CATEGORY_ID = 1L;

        @Test
        void shouldDeleteCategorySuccessfully() {
            Category category = new Category(CATEGORY_ID, "Books");
            when(categoryService.findById(CATEGORY_ID)).thenReturn(category);
            categoryUseCase.delete(CATEGORY_ID);
            verify(categoryService, times(1)).findById(CATEGORY_ID);
            verify(categoryValidator, times(1)).validateOnDelete(category);
            verify(categoryService, times(1)).delete(category);
        }

        @Test
        void shouldThrowExceptionWhenCategoryDoesNotExist() {
            when(categoryService.findById(CATEGORY_ID))
                    .thenThrow(new RecordNotFoundException("Category not found"));
            RecordNotFoundException e = assertThrows(
                    RecordNotFoundException.class,
                    () -> categoryUseCase.delete(CATEGORY_ID)
            );
            assertEquals("Category not found", e.getMessage());
            verify(categoryService, times(1)).findById(CATEGORY_ID);
            verify(categoryValidator, never()).validateOnDelete(any());
            verify(categoryService, never()).delete(any());
        }

        @Test
        void shouldNotDeleteCategoryWhenValidationFails() {
            Category category = new Category(CATEGORY_ID, "Books");
            when(categoryService.findById(CATEGORY_ID)).thenReturn(category);
            doThrow(new BusinessException("Cannot delete a category that has subcategories"))
                    .when(categoryValidator).validateOnDelete(category);
            BusinessException e = assertThrows(
                    BusinessException.class,
                    () -> categoryUseCase.delete(CATEGORY_ID)
            );
            assertEquals("Cannot delete a category that has subcategories", e.getMessage());
            verify(categoryService, times(1)).findById(CATEGORY_ID);
            verify(categoryValidator, times(1)).validateOnDelete(category);
            verify(categoryService, never()).delete(category);
        }
    }
}