package io.github.gabrielvelosoo.productservice.unit.infrastructure.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.gabrielvelosoo.productservice.application.dto.category.CategoryResponseDTO;
import io.github.gabrielvelosoo.productservice.application.dto.common.PageResponse;
import io.github.gabrielvelosoo.productservice.application.dto.product.ProductCreateDTO;
import io.github.gabrielvelosoo.productservice.application.dto.product.ProductFilterDTO;
import io.github.gabrielvelosoo.productservice.application.dto.product.ProductResponseDTO;
import io.github.gabrielvelosoo.productservice.application.dto.product.ProductUpdateDTO;
import io.github.gabrielvelosoo.productservice.application.usecase.product.ProductUseCase;
import io.github.gabrielvelosoo.productservice.infrastructure.controller.product.ProductController;
import io.github.gabrielvelosoo.productservice.infrastructure.exception.handler.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @Mock
    ProductUseCase productUseCase;

    @InjectMocks
    ProductController productController;

    MockMvc mockMvc;
    ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(productController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper().findAndRegisterModules();
    }

    @Nested
    class CreateTests {

        @Test
        void shouldCreateProductSuccessfully() throws Exception {
            MockMultipartFile image = buildMockMultipartFile();
            ProductResponseDTO responseDTO = buildProductResponseDTO();
            when(productUseCase.create(any(ProductCreateDTO.class))).thenReturn(responseDTO);
            mockMvc.perform(multipart("/api/v1/products")
                            .file(image)
                            .param("name", "Notebook")
                            .param("description", "Gaming notebook")
                            .param("price", "5999.90")
                            .param("stockQuantity", "10")
                            .param("categoryId", "1"))
                    .andExpect(status().isCreated())
                    .andExpect(header().string("Location",
                            containsString("/api/v1/products/1")))
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.name").value("Notebook"))
                    .andExpect(jsonPath("$.description").value("Gaming notebook"))
                    .andExpect(jsonPath("$.price").value(5999.90))
                    .andExpect(jsonPath("$.stockQuantity").value(10))
                    .andExpect(jsonPath("$.imageUrl").value("http://localhost/images/img.jpg"))
                    .andExpect(jsonPath("$.category.name").value("Electronics"))
                    .andExpect(jsonPath("$.categoryPath").value("Electronics > Notebook"));
            verify(productUseCase, times(1)).create(any(ProductCreateDTO.class));
        }
    }

    @Nested
    class GetByFilterTests {

        @Test
        void shouldReturnProductsSuccessfully() throws Exception {
            ProductResponseDTO responseDTO = buildProductResponseDTO();
            PageResponse<ProductResponseDTO> pageResponse =
                    new PageResponse<>(
                            List.of(responseDTO),
                            0,
                            10,
                            1,
                            1,
                            false,
                            false
                    );
            when(productUseCase.getByFilter(any(ProductFilterDTO.class), eq(0), eq(10))).thenReturn(pageResponse);
            mockMvc.perform(get("/api/v1/products")
                            .param("page", "0")
                            .param("page-size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content.length()").value(1))
                    .andExpect(jsonPath("$.content[0].id").value(1L))
                    .andExpect(jsonPath("$.content[0].name").value("Notebook"))
                    .andExpect(jsonPath("$.totalElements").value(1))
                    .andExpect(jsonPath("$.totalPages").value(1))
                    .andExpect(jsonPath("$.hasNext").value(false));
            verify(productUseCase, times(1)).getByFilter(any(ProductFilterDTO.class), eq(0), eq(10));
        }
    }

    @Nested
    class UpdateTests {

        @Test
        void shouldUpdateProductSuccessfully() throws Exception {
            MockMultipartFile image = buildMockMultipartFile();
            ProductResponseDTO responseDTO = buildProductResponseDTO();
            when(productUseCase.update(eq(1L), any(ProductUpdateDTO.class))).thenReturn(responseDTO);
            mockMvc.perform(multipart("/api/v1/products/{id}", 1L)
                            .file(image)
                            .with(req -> {
                                req.setMethod("PUT");
                                return req;
                            })
                            .param("name", "Notebook atualizado"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.name").value("Notebook"));
            verify(productUseCase, times(1)).update(eq(1L), any(ProductUpdateDTO.class));
        }
    }

    @Nested
    class DeleteTests {

        @Test
        void shouldDeleteProductSuccessfully() throws Exception {
            mockMvc.perform(delete("/api/v1/products/{id}", 1L))
                    .andExpect(status().isNoContent())
                    .andExpect(content().string(""));
            verify(productUseCase, times(1)).delete(1L);
        }
    }

    private ProductResponseDTO buildProductResponseDTO() {
        CategoryResponseDTO category =
                new CategoryResponseDTO(
                        1L,
                        "Electronics",
                        "electronics",
                        List.of()
                );
        return new ProductResponseDTO(
                1L,
                "Notebook",
                "Gaming notebook",
                BigDecimal.valueOf(5999.90),
                10,
                "http://localhost/images/img.jpg",
                category,
                "Electronics > Notebook"
        );
    }

    private MockMultipartFile buildMockMultipartFile() {
        return new MockMultipartFile(
                "image",
                "image.jpg",
                "image/jpeg",
                "fake-image".getBytes()
        );
    }
}