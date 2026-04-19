package io.github.gabrielvelosoo.productservice.unit.infrastructure.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.gabrielvelosoo.productservice.application.dto.category.CategoryRequestDTO;
import io.github.gabrielvelosoo.productservice.application.dto.category.CategoryResponseDTO;
import io.github.gabrielvelosoo.productservice.application.usecase.category.CategoryUseCase;
import io.github.gabrielvelosoo.productservice.infrastructure.controller.category.CategoryController;
import io.github.gabrielvelosoo.productservice.infrastructure.exception.handler.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CategoryControllerTest {

    @Mock
    CategoryUseCase categoryUseCase;

    @InjectMocks
    CategoryController categoryController;

    MockMvc mockMvc;
    ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(categoryController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .alwaysDo(print())
                .build();
        objectMapper = new ObjectMapper().findAndRegisterModules();
    }

    @Nested
    class CreateTests {

        @Test
        void shouldCreateCategorySuccessfully() throws Exception {
            CategoryRequestDTO requestDTO = new CategoryRequestDTO("Books", null);
            CategoryResponseDTO responseDTO = new CategoryResponseDTO(
                    1L, "Books", "books", List.of()
            );
            when(categoryUseCase.create(requestDTO)).thenReturn(responseDTO);
            mockMvc.perform(post("/api/v1/categories")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDTO)))
                    .andExpect(status().isCreated())
                    .andExpect(header().string("Location", containsString("/api/v1/categories/1")))
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.name").value("Books"))
                    .andExpect(jsonPath("$.slug").value("books"));
            verify(categoryUseCase, times(1)).create(requestDTO);
        }

        @Test
        void shouldCreateCategoryWithParentSuccessfully() throws Exception {
            Long parentId = 10L;
            CategoryRequestDTO requestDTO = new CategoryRequestDTO("Laptops", parentId);
            CategoryResponseDTO responseDTO = new CategoryResponseDTO(
                    2L, "Laptops", "laptops", List.of()
            );
            when(categoryUseCase.create(requestDTO)).thenReturn(responseDTO);
            mockMvc.perform(post("/api/v1/categories")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDTO)))
                    .andExpect(status().isCreated())
                    .andExpect(header().string("Location", containsString("/api/v1/categories/2")))
                    .andExpect(jsonPath("$.id").value(2L))
                    .andExpect(jsonPath("$.name").value("Laptops"))
                    .andExpect(jsonPath("$.slug").value("laptops"))
                    .andExpect(jsonPath("$.subcategories").isArray())
                    .andExpect(jsonPath("$.subcategories").isEmpty());
            verify(categoryUseCase, times(1)).create(requestDTO);
        }

        @Test
        void shouldReturn422WhenNameIsBlank() throws Exception {
            CategoryRequestDTO requestDTO = new CategoryRequestDTO("", null);
            mockMvc.perform(post("/api/v1/categories")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDTO)))
                    .andExpect(status().isUnprocessableEntity())
                    .andExpect(jsonPath("$.status").value(422))
                    .andExpect(jsonPath("$.errors").isArray())
                    .andExpect(jsonPath("$.errors[0].field").value("name"))
                    .andExpect(jsonPath("$.errors[0].message").value("Required field"));
            verifyNoInteractions(categoryUseCase);
        }

        @Test
        void shouldReturn422WhenNameIsMissing() throws Exception {
            String invalidJson = """
                {
                    "parentCategoryId": 1
                }
                """;
            mockMvc.perform(post("/api/v1/categories")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(invalidJson))
                    .andExpect(status().isUnprocessableEntity())
                    .andExpect(jsonPath("$.status").value(422))
                    .andExpect(jsonPath("$.errors").isArray())
                    .andExpect(jsonPath("$.errors[0].field").value("name"))
                    .andExpect(jsonPath("$.errors[0].message").value("Required field"));
            verifyNoInteractions(categoryUseCase);
        }
    }

    @Nested
    class GetRootCategoriesTests {

        @Test
        void shouldReturnRootCategoriesSuccessfully() throws Exception {
            CategoryResponseDTO child = new CategoryResponseDTO(
                    2L, "Child", "child", List.of()
            );
            CategoryResponseDTO root = new CategoryResponseDTO(
                    1L, "Root", "root", List.of(child)
            );
            when(categoryUseCase.getRootCategories()).thenReturn(List.of(root));
            mockMvc.perform(get("/api/v1/categories")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].id").value(1L))
                    .andExpect(jsonPath("$[0].name").value("Root"))
                    .andExpect(jsonPath("$[0].slug").value("root"))
                    .andExpect(jsonPath("$[0].subcategories").isArray())
                    .andExpect(jsonPath("$[0].subcategories.length()").value(1))
                    .andExpect(jsonPath("$[0].subcategories[0].name").value("Child"));
            verify(categoryUseCase, times(1)).getRootCategories();
        }
    }

    @Nested
    class DeleteTests {

        @Test
        void shouldDeleteCategorySuccessfully() throws Exception {
            Long categoryId = 1L;
            mockMvc.perform(delete("/api/v1/categories/{id}", categoryId))
                    .andExpect(status().isNoContent())
                    .andExpect(content().string(""));
            verify(categoryUseCase, times(1)).delete(categoryId);
        }
    }
}