package io.github.gabrielvelosoo.cartservice.infrastructure.controller;

import io.github.gabrielvelosoo.cartservice.application.dto.cart.CartResponseDTO;
import io.github.gabrielvelosoo.cartservice.application.dto.cart.CartItemRequestDTO;
import io.github.gabrielvelosoo.cartservice.application.dto.checkout.CheckoutResponseDTO;
import io.github.gabrielvelosoo.cartservice.application.usecase.CartUseCase;
import io.github.gabrielvelosoo.cartservice.application.usecase.CheckoutCartUseCase;
import io.github.gabrielvelosoo.cartservice.application.validator.group.ValidationOrder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/v1/cart")
public class CartController {

    private static final Logger logger = LogManager.getLogger(CartController.class);

    private final CartUseCase cartUseCase;
    private final CheckoutCartUseCase checkoutCartUseCase;

    public CartController(CartUseCase cartUseCase, CheckoutCartUseCase checkoutCartUseCase) {
        this.cartUseCase = cartUseCase;
        this.checkoutCartUseCase = checkoutCartUseCase;
    }

    @GetMapping
    public ResponseEntity<CartResponseDTO> getCart() {
        logger.info("[GetCart] Request received");
        CartResponseDTO response = cartUseCase.getCart();
        logger.info("[GetCart] Response ready for userId='{}'", response.userId());
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/items")
    public ResponseEntity<CartResponseDTO> addItem(@RequestBody @Validated(ValidationOrder.class) CartItemRequestDTO request) {
        logger.info("[AddItem] Request received for productId='{}', quantity='{}'",
                request.productId(), request.quantity());
        CartResponseDTO response = cartUseCase.addItem(request);
        logger.info("[AddItem] Item added successfully for userId='{}', productId='{}'",
                response.userId(), request.productId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping(value = "/items")
    public ResponseEntity<CartResponseDTO> updateItem(@RequestBody @Validated(ValidationOrder.class) CartItemRequestDTO request) {
        logger.info("[UpdateItem] Request received for productId='{}', quantity='{}'",
                request.productId(), request.quantity());
        CartResponseDTO response = cartUseCase.updateItem(request);
        logger.info("[UpdateItem] Item updated successfully for userId='{}', productId='{}'",
                response.userId(), request.productId());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping(value = "/items/{productId}")
    public ResponseEntity<CartResponseDTO> removeItem(@PathVariable(name = "productId") Long productId) {
        logger.info("[RemoveItem] Request received for productId='{}'", productId);
        CartResponseDTO response = cartUseCase.removeItem(productId);
        logger.info("[RemoveItem] Item removed successfully for userId='{}', productId='{}'",
                response.userId(), productId);
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/checkout")
    public ResponseEntity<CheckoutResponseDTO> checkout() {
        logger.info("[Checkout] Request received");
        CheckoutResponseDTO response = checkoutCartUseCase.execute();
        logger.info("[Checkout] Checkout completed successfully. orderId='{}', status='{}'",
                response.id(), response.status());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
