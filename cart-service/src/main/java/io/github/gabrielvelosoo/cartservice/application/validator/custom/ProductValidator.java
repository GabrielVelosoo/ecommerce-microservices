package io.github.gabrielvelosoo.cartservice.application.validator.custom;

import io.github.gabrielvelosoo.cartservice.application.dto.product.ProductSnapshotDTO;
import io.github.gabrielvelosoo.cartservice.application.exception.BusinessException;
import io.github.gabrielvelosoo.cartservice.application.exception.RecordNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class ProductValidator {

    public void validateOnAddItem(ProductSnapshotDTO product, Integer quantity) {
        validateProductExists(product);
        validateStock(product, quantity);
    }

    public void validateOnUpdateItem(ProductSnapshotDTO product, Integer quantity) {
        validateProductExists(product);
        validateStock(product, quantity);
    }

    private void validateProductExists(ProductSnapshotDTO product) {
        if(product == null) {
            throw new RecordNotFoundException("Product not found");
        }
    }

    private void validateStock(ProductSnapshotDTO product, Integer quantity) {
        if(product.stockQuantity() <= 0) {
            throw new BusinessException("Product out of stock");
        }
        if(quantity > product.stockQuantity()) {
            throw new BusinessException("Requested quantity exceeds stock");
        }
    }
}
