package io.github.gabrielvelosoo.productservice.infrastructure.controller;

import io.github.gabrielvelosoo.productservice.application.dto.common.PageResponse;
import io.github.gabrielvelosoo.productservice.application.dto.product.ProductCreateDTO;
import io.github.gabrielvelosoo.productservice.application.dto.product.ProductFilterDTO;
import io.github.gabrielvelosoo.productservice.application.dto.product.ProductResponseDTO;
import io.github.gabrielvelosoo.productservice.application.dto.product.ProductUpdateDTO;
import io.github.gabrielvelosoo.productservice.application.usecase.product.ProductUseCase;
import io.github.gabrielvelosoo.productservice.application.validator.group.ValidationOrder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URI;

@RestController
@RequestMapping(value = "/api/v1/products")
public class ProductController implements GenericController {

    private static final Logger logger = LogManager.getLogger(ProductController.class);

    private final ProductUseCase productUseCase;

    public ProductController(ProductUseCase productUseCase) {
        this.productUseCase = productUseCase;
    }

    @PostMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponseDTO> create(
            @ModelAttribute
            @Validated(ValidationOrder.class)
            ProductCreateDTO productCreateDTO
    ) throws IOException {
        logger.info("Received request to create new product");
        ProductResponseDTO createdProductDTO = productUseCase.create(productCreateDTO);
        logger.info("Product id='{}' created successfully", createdProductDTO.id());
        URI location = generateHeaderLocation(createdProductDTO.id());
        return ResponseEntity.created(location).body(createdProductDTO);
    }

    @GetMapping(value = "/{id}")
    @PreAuthorize("hasRole('PRODUCT_READ')")
    public ResponseEntity<ProductResponseDTO> findById(@PathVariable(name = "id") Long productId) {
        logger.info("Received request to find product by id='{}'", productId);
        ProductResponseDTO responseDTO = productUseCase.findById(productId);
        logger.info("Product id='{}' found successfully", productId);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping
    public ResponseEntity<PageResponse<ProductResponseDTO>> getByFilter(
            ProductFilterDTO productFilterDTO,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "page-size", defaultValue = "10") Integer pageSize
    ) {
        logger.info("Received request to get products by filter='{}'", productFilterDTO);
        PageResponse<ProductResponseDTO> result = productUseCase.getByFilter(productFilterDTO, page, pageSize);
        logger.info("Products retrieved successfully. '{}' products found", result.content().size());
        return ResponseEntity.ok(result);
    }

    @PutMapping(value = "/{id}", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponseDTO> update(
            @PathVariable(name = "id") Long productId,
            @ModelAttribute @Validated(ValidationOrder.class) ProductUpdateDTO productUpdateDTO
    ) throws IOException {
        logger.info("Received request to update product id='{}'", productId);
        ProductResponseDTO updatedProductDTO = productUseCase.update(productId, productUpdateDTO);
        logger.info("Product id='{}' updated successfully", productId);
        return ResponseEntity.ok(updatedProductDTO);
    }

    @DeleteMapping(value = "/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable(name = "id") Long productId) throws IOException {
        logger.info("Received request to delete product id='{}'", productId);
        productUseCase.delete(productId);
        logger.info("Product id='{}' deleted successfully", productId);
        return ResponseEntity.noContent().build();
    }
}
