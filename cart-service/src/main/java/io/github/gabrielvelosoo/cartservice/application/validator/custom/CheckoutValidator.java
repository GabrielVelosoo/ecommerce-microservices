package io.github.gabrielvelosoo.cartservice.application.validator.custom;

import io.github.gabrielvelosoo.cartservice.application.dto.product.ProductSnapshotDTO;
import io.github.gabrielvelosoo.cartservice.application.exception.BusinessException;
import io.github.gabrielvelosoo.cartservice.application.exception.RecordNotFoundException;
import io.github.gabrielvelosoo.cartservice.domain.entity.Cart;
import org.springframework.stereotype.Component;

@Component
public class CheckoutValidator {

    public void validateCart(Cart cart) {
        if(cart.isEmpty()) {
            throw new BusinessException("Cannot checkout an empty cart");
        }
    }

    public void validateProductForCheckout(ProductSnapshotDTO product, Integer quantity) {
        if(product == null) {
            throw new RecordNotFoundException("Product not found");
        }
        if(product.stockQuantity() < quantity) {
            throw new BusinessException("Insufficient stock for product " + product.id());
        }
    }
}
