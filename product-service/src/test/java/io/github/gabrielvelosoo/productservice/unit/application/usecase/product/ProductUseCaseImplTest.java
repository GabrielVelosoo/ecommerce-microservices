package io.github.gabrielvelosoo.productservice.unit.application.usecase.product;

import io.github.gabrielvelosoo.productservice.application.dto.common.PageResponse;
import io.github.gabrielvelosoo.productservice.application.dto.product.ProductCreateDTO;
import io.github.gabrielvelosoo.productservice.application.dto.product.ProductFilterDTO;
import io.github.gabrielvelosoo.productservice.application.dto.product.ProductResponseDTO;
import io.github.gabrielvelosoo.productservice.application.dto.product.ProductUpdateDTO;
import io.github.gabrielvelosoo.productservice.application.mapper.PageMapper;
import io.github.gabrielvelosoo.productservice.application.mapper.ProductMapper;
import io.github.gabrielvelosoo.productservice.application.usecase.product.ProductUseCaseImpl;
import io.github.gabrielvelosoo.productservice.application.validator.custom.ProductValidator;
import io.github.gabrielvelosoo.productservice.domain.entity.Product;
import io.github.gabrielvelosoo.productservice.domain.service.ImageStorageService;
import io.github.gabrielvelosoo.productservice.domain.service.ProductService;
import io.github.gabrielvelosoo.productservice.application.exception.RecordNotFoundException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;

import java.io.IOException;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductUseCaseImplTest {

    @Mock
    ProductService productService;

    @Mock
    ProductValidator productValidator;

    @Mock
    ImageStorageService imageStorageService;

    @Mock
    ProductMapper productMapper;

    @Mock
    PageMapper pageMapper;

    @InjectMocks
    ProductUseCaseImpl productUseCase;

    @Nested
    class CreateTests {

        @Test
        void shouldCreateProductSuccessfully() throws IOException {
            ProductCreateDTO requestDTO = mock(ProductCreateDTO.class);
            Product product = new Product();
            Product savedProduct = new Product();
            ProductResponseDTO responseDTO = mock(ProductResponseDTO.class);
            when(productMapper.toEntity(requestDTO)).thenReturn(product);
            when(imageStorageService.save(requestDTO.image())).thenReturn("image-url");
            when(productService.save(product)).thenReturn(savedProduct);
            when(productMapper.toDTO(savedProduct)).thenReturn(responseDTO);
            ProductResponseDTO result = productUseCase.create(requestDTO);
            assertNotNull(result);
            verify(productMapper, times(1)).toEntity(requestDTO);
            verify(productValidator, times(1)).validateOnCreateAndUpdate(product);
            verify(imageStorageService, times(1)).save(requestDTO.image());
            verify(productService, times(1)).save(product);
            verify(productMapper, times(1)).toDTO(savedProduct);
        }

        @Test
        void shouldThrowExceptionWhenValidationFailsOnCreate() {
            ProductCreateDTO requestDTO = mock(ProductCreateDTO.class);
            Product product = new Product();
            when(productMapper.toEntity(requestDTO)).thenReturn(product);
            doThrow(new RecordNotFoundException("Category not found"))
                    .when(productValidator)
                    .validateOnCreateAndUpdate(product);
            assertThrows(
                    RecordNotFoundException.class,
                    () -> productUseCase.create(requestDTO)
            );
            verify(productService, never()).save(any());
        }
    }

    @Nested
    class GetByFilterTests {

        @Test
        void shouldReturnPagedProductsByFilter() {
            ProductFilterDTO filterDTO = mock(ProductFilterDTO.class);
            @SuppressWarnings("unchecked")
            Page<Product> page = (Page<Product>) mock(Page.class);
            @SuppressWarnings("unchecked")
            PageResponse<ProductResponseDTO> pageResponse = (PageResponse<ProductResponseDTO>) mock(PageResponse.class);
            when(productService.findAll(any(), any())).thenReturn(page);
            when(pageMapper.toPageResponse(
                    eq(page),
                    ArgumentMatchers.<Function<Product, ProductResponseDTO>>any()
            )).thenReturn(pageResponse);
            PageResponse<ProductResponseDTO> result = productUseCase.getByFilter(filterDTO, 0, 10);
            assertNotNull(result);
            assertSame(pageResponse, result);
            verify(productService).findAll(any(), any());
            verify(pageMapper).toPageResponse(eq(page), any());
        }
    }

    @Nested
    class UpdateTests {

        @Test
        void shouldUpdateProductSuccessfully() throws IOException {
            Long productId = 1L;
            ProductUpdateDTO requestDTO = mock(ProductUpdateDTO.class);
            Product product = new Product();
            Product updatedProduct = new Product();
            ProductResponseDTO responseDTO = mock(ProductResponseDTO.class);
            when(productService.findById(productId)).thenReturn(product);
            when(productService.save(product)).thenReturn(updatedProduct);
            when(productMapper.toDTO(updatedProduct)).thenReturn(responseDTO);
            ProductResponseDTO result = productUseCase.update(productId, requestDTO);
            assertNotNull(result);
            verify(productService, times(1)).findById(productId);
            verify(productValidator, times(1)).validateOnCreateAndUpdate(product);
            verify(productMapper, times(1)).update(product, requestDTO);
            verify(productService, times(1)).save(product);
            verify(productMapper, times(1)).toDTO(updatedProduct);
        }
    }

    @Nested
    class DeleteTests {

        @Test
        void shouldDeleteProductSuccessfully() throws IOException {
            Long productId = 1L;
            Product product = new Product();
            product.setImageUrl("image-url");
            when(productService.findById(productId)).thenReturn(product);
            productUseCase.delete(productId);
            verify(productService, times(1)).findById(productId);
            verify(imageStorageService, times(1)).delete("image-url");
            verify(productService, times(1)).delete(product);
        }
    }
}