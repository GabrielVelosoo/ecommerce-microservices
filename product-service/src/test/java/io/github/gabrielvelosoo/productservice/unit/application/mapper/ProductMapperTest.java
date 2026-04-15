package io.github.gabrielvelosoo.productservice.unit.application.mapper;

import io.github.gabrielvelosoo.productservice.application.dto.product.ProductResponseDTO;
import io.github.gabrielvelosoo.productservice.application.dto.product.ProductUpdateDTO;
import io.github.gabrielvelosoo.productservice.application.mapper.CategoryMapper;
import io.github.gabrielvelosoo.productservice.application.mapper.ProductMapper;
import io.github.gabrielvelosoo.productservice.domain.entity.Category;
import io.github.gabrielvelosoo.productservice.domain.entity.Product;
import io.github.gabrielvelosoo.productservice.domain.service.CategoryService;
import io.github.gabrielvelosoo.productservice.domain.service.ImageStorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductMapperTest {

    ProductMapper productMapper;

    @Mock
    CategoryService categoryService;

    @Mock
    ImageStorageService imageStorageService;

    @BeforeEach
    void setUp() {
        productMapper = Mappers.getMapper(ProductMapper.class);
        CategoryMapper categoryMapper = Mappers.getMapper(CategoryMapper.class);
        ReflectionTestUtils.setField(productMapper, "categoryMapper", categoryMapper);
        ReflectionTestUtils.setField(productMapper, "categoryService", categoryService);
        ReflectionTestUtils.setField(productMapper, "imageStorageService", imageStorageService);
    }

    @Nested
    class UpdateTests {

        @Test
        void shouldUpdateBasicFieldsSuccessfully() throws IOException {
            Product product = new Product();
            product.setName("Old name");
            product.setDescription("Old desc");
            product.setPrice(BigDecimal.TEN);
            product.setStockQuantity(5);
            ProductUpdateDTO requestDTO = mock(ProductUpdateDTO.class);
            when(requestDTO.name()).thenReturn("New name");
            when(requestDTO.description()).thenReturn("New desc");
            when(requestDTO.price()).thenReturn(BigDecimal.valueOf(99));
            when(requestDTO.stockQuantity()).thenReturn(10);
            when(requestDTO.categoryId()).thenReturn(null);
            when(requestDTO.image()).thenReturn(null);
            productMapper.update(product, requestDTO);
            assertEquals("New name", product.getName());
            assertEquals("New desc", product.getDescription());
            assertEquals(BigDecimal.valueOf(99), product.getPrice());
            assertEquals(10, product.getStockQuantity());
            verifyNoInteractions(categoryService);
            verifyNoInteractions(imageStorageService);
        }

        @Test
        void shouldUpdateCategoryWhenCategoryIdIsProvided() throws IOException {
            Product product = new Product();
            ProductUpdateDTO requestDTO = mock(ProductUpdateDTO.class);
            when(requestDTO.categoryId()).thenReturn(10L);
            when(requestDTO.image()).thenReturn(null);
            Category category = new Category();
            category.setId(10L);
            when(categoryService.findById(10L)).thenReturn(category);
            productMapper.update(product, requestDTO);
            assertEquals(category, product.getCategory());
            verify(categoryService, times(1)).findById(10L);
            verifyNoInteractions(imageStorageService);
        }

        @Test
        void shouldReplaceImageWhenImageIsProvided() throws IOException {
            Product product = new Product();
            product.setImageUrl("old-image");
            ProductUpdateDTO requestDTO = mock(ProductUpdateDTO.class);
            when(requestDTO.image()).thenReturn(mock(MultipartFile.class));
            when(requestDTO.categoryId()).thenReturn(null);
            when(imageStorageService.replace("old-image", requestDTO.image()))
                    .thenReturn("new-image");
            productMapper.update(product, requestDTO);
            assertEquals("new-image", product.getImageUrl());
            verify(imageStorageService).replace("old-image", requestDTO.image());
            verifyNoInteractions(categoryService);
        }

        @Test
        void shouldNotReplaceImageWhenImageIsEmpty() throws IOException {
            Product product = new Product();
            product.setImageUrl("old-image");
            MultipartFile emptyFile = mock(MultipartFile.class);
            when(emptyFile.isEmpty()).thenReturn(true);
            ProductUpdateDTO dto = mock(ProductUpdateDTO.class);
            when(dto.image()).thenReturn(emptyFile);
            when(dto.categoryId()).thenReturn(null);
            productMapper.update(product, dto);
            assertEquals("old-image", product.getImageUrl());
            verifyNoInteractions(imageStorageService);
            verifyNoInteractions(categoryService);
        }
    }

    @Nested
    class BuildCategoryPathTests {

        @Test
        void shouldBuildCategoryPathWithParent() {
            Category parent = new Category();
            parent.setName("Electronics");
            Category category = new Category();
            category.setName("Notebook");
            category.setParentCategory(parent);
            Product product = new Product();
            product.setCategory(category);
            ProductResponseDTO responseDTO = productMapper.toDTO(product);
            assertEquals("Electronics > Notebook", responseDTO.categoryPath());
        }

        @Test
        void shouldBuildCategoryPathWithoutParent() {
            Category category = new Category();
            category.setName("Books");
            Product product = new Product();
            product.setCategory(category);
            ProductResponseDTO responseDTO = productMapper.toDTO(product);
            assertEquals("Books", responseDTO.categoryPath());
        }
    }
}