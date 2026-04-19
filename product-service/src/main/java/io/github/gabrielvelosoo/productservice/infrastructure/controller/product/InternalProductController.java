package io.github.gabrielvelosoo.productservice.infrastructure.controller.product;

import io.github.gabrielvelosoo.productservice.application.dto.product.ProductResponseDTO;
import io.github.gabrielvelosoo.productservice.application.usecase.product.ProductUseCase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v1/internal/products")
public class InternalProductController {

    private static final Logger logger = LogManager.getLogger(InternalProductController.class);

    private final ProductUseCase productUseCase;

    public InternalProductController(ProductUseCase productUseCase) {
        this.productUseCase = productUseCase;
    }

    @GetMapping(value = "/{id}")
    @PreAuthorize("hasRole('PRODUCT_READ')")
    public ResponseEntity<ProductResponseDTO> findByIdInternal(@PathVariable(name = "id") Long productId) {
        logger.info("[HTTP GET /internal/products/{}] Request received", productId);
        ProductResponseDTO response = productUseCase.findById(productId);
        logger.info("[HTTP GET /internal/products/{}] OK", productId);
        return ResponseEntity.ok(response);
    }
}
