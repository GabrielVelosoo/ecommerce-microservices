package io.github.gabrielvelosoo.productservice.unit.infrastructure.service;

import io.github.gabrielvelosoo.productservice.domain.entity.Product;
import io.github.gabrielvelosoo.productservice.infrastructure.persistence.repository.ProductRepository;
import io.github.gabrielvelosoo.productservice.infrastructure.exception.RecordNotFoundException;
import io.github.gabrielvelosoo.productservice.infrastructure.service.ProductServiceImpl;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    ProductRepository productRepository;

    @InjectMocks
    ProductServiceImpl productService;

    @Nested
    class SaveTests {

        @Test
        void shouldSaveProductSuccessfully() {
            Product product = new Product();
            product.setName("Notebook");
            when(productRepository.save(product))
                    .thenAnswer(invocation -> invocation.getArgument(0));
            Product savedProduct = productService.save(product);
            assertNotNull(savedProduct);
            assertSame(product, savedProduct);
            assertEquals("Notebook", savedProduct.getName());
            verify(productRepository, times(1)).save(product);
        }
    }

    @Nested
    class FindByIdTests {

        private static final Long PRODUCT_ID = 1L;

        @Test
        void shouldFindProductByIdSuccessfully() {
            Product product = new Product();
            product.setId(1L);
            when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(product));
            Product foundProduct = productService.findById(PRODUCT_ID);
            assertNotNull(foundProduct);
            assertEquals(PRODUCT_ID, foundProduct.getId());
            verify(productRepository, times(1)).findById(PRODUCT_ID);
        }

        @Test
        void shouldThrowExceptionWhenProductDoesNotExist() {
            when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.empty());
            RecordNotFoundException e = assertThrows(
                    RecordNotFoundException.class,
                    () -> productService.findById(PRODUCT_ID)
            );
            assertEquals("Product not found", e.getMessage());
            verify(productRepository, times(1)).findById(PRODUCT_ID);
        }
    }

    @Nested
    class FindAllTests {

        @Test
        void shouldFindAllProductsSuccessfully() {
            @SuppressWarnings("unchecked")
            Specification<Product> spec = (Specification<Product>) mock(Specification.class);
            Pageable pageable = PageRequest.of(0, 10);
            Product product = new Product();
            product.setName("Notebook");
            Page<Product> page = new PageImpl<>(List.of(product), pageable, 1);
            when(productRepository.findAll(spec, pageable)).thenReturn(page);
            Page<Product> result = productService.findAll(spec, pageable);
            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
            assertEquals("Notebook", result.getContent().getFirst().getName());
            verify(productRepository, times(1)).findAll(spec, pageable);
        }
    }

    @Nested
    class DeleteTests {

        @Test
        void shouldDeleteProductSuccessfully() {
            Product product = new Product();
            productService.delete(product);
            verify(productRepository, times(1)).delete(product);
        }
    }
}