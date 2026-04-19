package io.github.gabrielvelosoo.productservice.infrastructure.controller.category;

import io.github.gabrielvelosoo.productservice.application.dto.category.CategoryRequestDTO;
import io.github.gabrielvelosoo.productservice.application.dto.category.CategoryResponseDTO;
import io.github.gabrielvelosoo.productservice.application.usecase.category.CategoryUseCase;
import io.github.gabrielvelosoo.productservice.application.validator.group.ValidationOrder;
import io.github.gabrielvelosoo.productservice.infrastructure.controller.GenericController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<CategoryResponseDTO> create(
            @RequestBody
            @Validated(ValidationOrder.class)
            CategoryRequestDTO request
    ) {
        logger.info("[HTTP POST /categories] Request received");
        CategoryResponseDTO response = categoryUseCase.create(request);
        URI location = generateHeaderLocation(response.id());
        logger.info("[HTTP POST /categories] CREATED. categoryId='{}'", response.id());
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponseDTO>> getRootCategories() {
        List<CategoryResponseDTO> result = categoryUseCase.getRootCategories();
        return ResponseEntity.ok(result);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable(name = "id") Long categoryId) {
        logger.info("[HTTP DELETE /categories/{}] Request received", categoryId);
        categoryUseCase.delete(categoryId);
        logger.info("[HTTP DELETE /categories/{}] NO_CONTENT", categoryId);
        return ResponseEntity.noContent().build();
    }
}
