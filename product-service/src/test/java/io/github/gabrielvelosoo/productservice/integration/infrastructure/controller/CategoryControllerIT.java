package io.github.gabrielvelosoo.productservice.integration.infrastructure.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.gabrielvelosoo.productservice.application.dto.category.CategoryRequestDTO;
import io.github.gabrielvelosoo.productservice.domain.entity.Category;
import io.github.gabrielvelosoo.productservice.infrastructure.persistence.repository.CategoryRepository;
import io.github.gabrielvelosoo.productservice.integration.configuration.AbstractIntegrationTest;
import io.github.gabrielvelosoo.productservice.integration.configuration.TestSecurityConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
class CategoryControllerIT extends AbstractIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    CategoryRepository categoryRepository;

    @MockitoBean
    JwtDecoder jwtDecoder;

    @AfterEach
    void tearDown() {
        categoryRepository.deleteAll();
    }

    @Nested
    class CreateTests {

        @Test
        void shouldCreateCategorySuccessfully() throws Exception {
            CategoryRequestDTO requestDTO = new CategoryRequestDTO("Books", null);
            mockMvc.perform(post("/api/v1/categories")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDTO)))
                    .andExpect(status().isCreated())
                    .andExpect(header().exists("Location"))
                    .andExpect(jsonPath("$.id").isNumber())
                    .andExpect(jsonPath("$.name").value("Books"))
                    .andExpect(jsonPath("$.slug").value("books"))
                    .andExpect(jsonPath("$.subcategories").isArray())
                    .andExpect(jsonPath("$.subcategories").isEmpty());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void shouldReturn422WhenNameIsBlank() throws Exception {
            CategoryRequestDTO requestDTO = new CategoryRequestDTO("", null);
            mockMvc.perform(post("/api/v1/categories")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDTO)))
                    .andExpect(status().isUnprocessableEntity())
                    .andExpect(jsonPath("$.errors").isArray());
        }
    }

    @Nested
    class GetRootCategoriesTests {

        @Test
        void shouldReturnOnlyRootCategories() throws Exception {
            Category root = persistCategory("Root", null);
            persistCategory("Child", root);
            mockMvc.perform(get("/api/v1/categories"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].name").value("Root"))
                    .andExpect(jsonPath("$[0].subcategories.length()").value(1));
        }

        @Test
        void shouldReturnEmptyWhenNoRootCategories() throws Exception {
            mockMvc.perform(get("/api/v1/categories"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(0));
        }
    }

    @Nested
    class DeleteTests {

        @Test
        @WithMockUser(roles = "ADMIN")
        void shouldDeleteCategorySuccessfully() throws Exception {
            Category category = persistCategory("DeleteMe", null);
            mockMvc.perform(delete("/api/v1/categories/{id}", category.getId()))
                    .andExpect(status().isNoContent());
            assertThat(categoryRepository.findById(category.getId())).isEmpty();
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void shouldReturnNotFoundWhenCategoryDoesNotExist() throws Exception {
            mockMvc.perform(delete("/api/v1/categories/{id}", 999L))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value("Category not found"));
        }
    }

    private Category persistCategory(String name, Category parent) {
        Category category = new Category();
        category.setName(name);
        category.setParentCategory(parent);
        category.setSlug(category.generateSlug(name));
        return categoryRepository.save(category);
    }
}
