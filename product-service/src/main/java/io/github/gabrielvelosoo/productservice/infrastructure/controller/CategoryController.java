package io.github.gabrielvelosoo.productservice.infrastructure.controller;

import io.github.gabrielvelosoo.productservice.application.dto.category.CategoryRequestDTO;
import io.github.gabrielvelosoo.productservice.application.dto.category.CategoryResponseDTO;
import io.github.gabrielvelosoo.productservice.application.usecase.category.CategoryUseCase;
import io.github.gabrielvelosoo.productservice.application.validator.group.ValidationOrder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/categories")
public class CategoryController implements GenericController {

    private static final Logger logger = LogManager.getLogger(CategoryController.class);

    private final CategoryUseCase categoryUseCase;

    public CategoryController(CategoryUseCase categoryUseCase) {
        this.categoryUseCase = categoryUseCase;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryResponseDTO> create(
            @RequestBody
            @Validated(ValidationOrder.class)
            CategoryRequestDTO categoryRequestDTO
    ) {
        logger.info("Received request to create new category");
        CategoryResponseDTO createdCategoryDTO = categoryUseCase.create(categoryRequestDTO);
        logger.info("Category id='{}' created successfully", createdCategoryDTO.id());
        URI location = generateHeaderLocation(createdCategoryDTO.id());
        return ResponseEntity.created(location).body(createdCategoryDTO);
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponseDTO>> getRootCategories() {
        logger.info("Received request to get root categories");
        List<CategoryResponseDTO> result = categoryUseCase.getRootCategories();
        logger.info("Root categories retrieved successfully. '{}' categories found", result.size());
        return ResponseEntity.ok(result);
    }

    @DeleteMapping(value = "/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable(name = "id") Long categoryId) {
        logger.info("Received request to delete category id='{}'", categoryId);
        categoryUseCase.delete(categoryId);
        logger.info("Category id='{}' deleted successfully", categoryId);
        return ResponseEntity.noContent().build();
    }
}
