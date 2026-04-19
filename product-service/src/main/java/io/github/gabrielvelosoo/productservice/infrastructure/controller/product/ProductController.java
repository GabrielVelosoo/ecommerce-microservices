package io.github.gabrielvelosoo.productservice.infrastructure.controller.product;

import io.github.gabrielvelosoo.productservice.application.dto.common.PageResponse;
import io.github.gabrielvelosoo.productservice.application.dto.product.ProductCreateDTO;
import io.github.gabrielvelosoo.productservice.application.dto.product.ProductFilterDTO;
import io.github.gabrielvelosoo.productservice.application.dto.product.ProductResponseDTO;
import io.github.gabrielvelosoo.productservice.application.dto.product.ProductUpdateDTO;
import io.github.gabrielvelosoo.productservice.application.usecase.product.ProductUseCase;
import io.github.gabrielvelosoo.productservice.application.validator.group.ValidationOrder;
import io.github.gabrielvelosoo.productservice.infrastructure.controller.GenericController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<ProductResponseDTO> create(
            @ModelAttribute
            @Validated(ValidationOrder.class)
            ProductCreateDTO request
    ) throws IOException {
        logger.info("[HTTP POST /products] Request received");
        ProductResponseDTO response = productUseCase.create(request);
        URI location = generateHeaderLocation(response.id());
        logger.info("[HTTP POST /products] CREATED. productId='{}'", response.id());
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    public ResponseEntity<PageResponse<ProductResponseDTO>> getByFilter(
            ProductFilterDTO productFilterDTO,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "page-size", defaultValue = "10") Integer pageSize
    ) {
        PageResponse<ProductResponseDTO> result = productUseCase.getByFilter(productFilterDTO, page, pageSize);
        return ResponseEntity.ok(result);
    }

    @PutMapping(value = "/{id}", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<ProductResponseDTO> update(
            @PathVariable(name = "id") Long productId,
            @ModelAttribute @Validated(ValidationOrder.class) ProductUpdateDTO request
    ) throws IOException {
        logger.info("[HTTP PUT /products/{}] Request received", productId);
        ProductResponseDTO response = productUseCase.update(productId, request);
        logger.info("[HTTP PUT /products/{}] OK", productId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable(name = "id") Long productId) throws IOException {
        logger.info("[HTTP DELETE /products/{}] Request received", productId);
        productUseCase.delete(productId);
        logger.info("[HTTP DELETE /products/{}] NO_CONTENT", productId);
        return ResponseEntity.noContent().build();
    }
}
