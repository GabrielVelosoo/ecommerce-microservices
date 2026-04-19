package io.github.gabrielvelosoo.cartservice.application.usecase;

import io.github.gabrielvelosoo.cartservice.application.dto.checkout.CheckoutItemDTO;
import io.github.gabrielvelosoo.cartservice.application.dto.checkout.CheckoutRequestDTO;
import io.github.gabrielvelosoo.cartservice.application.dto.checkout.CheckoutResponseDTO;
import io.github.gabrielvelosoo.cartservice.application.dto.product.ProductSnapshotDTO;
import io.github.gabrielvelosoo.cartservice.application.gateway.OrderGateway;
import io.github.gabrielvelosoo.cartservice.application.gateway.ProductGateway;
import io.github.gabrielvelosoo.cartservice.application.validator.custom.CheckoutValidator;
import io.github.gabrielvelosoo.cartservice.domain.entity.Cart;
import io.github.gabrielvelosoo.cartservice.domain.entity.CartItem;
import io.github.gabrielvelosoo.cartservice.domain.service.AuthService;
import io.github.gabrielvelosoo.cartservice.domain.service.CartService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CheckoutCartUseCase {

    private static final Logger logger = LogManager.getLogger(CheckoutCartUseCase.class);

    private final CartService cartService;
    private final ProductGateway productGateway;
    private final OrderGateway orderGateway;
    private final AuthService authService;
    private final CheckoutValidator checkoutValidator;

    public CheckoutCartUseCase(CartService cartService, ProductGateway productGateway, OrderGateway orderGateway, AuthService authService, CheckoutValidator checkoutValidator) {
        this.cartService = cartService;
        this.productGateway = productGateway;
        this.orderGateway = orderGateway;
        this.authService = authService;
        this.checkoutValidator = checkoutValidator;
    }

    public CheckoutResponseDTO execute() {
        String userId = authService.getUserId();
        logger.info("[Checkout] Starting checkout. userId='{}'", userId);
        Cart cart = cartService.getOrInitialize(userId);
        checkoutValidator.validateCart(cart);
        logger.debug("[Checkout] Building checkout items. itemsCount='{}'",
                cart.getItems().size());
        List<CheckoutItemDTO> items = buildCheckoutItems(cart);
        logger.debug("[Checkout] Building request to create order");
        CheckoutRequestDTO request = new CheckoutRequestDTO(userId, items);
        logger.debug("[Checkout] Sending order creation request to order-service");
        CheckoutResponseDTO response = orderGateway.createOrder(request);
        logger.info("[Checkout] Order created successfully. orderId='{}', userId='{}'",
                response.id(), userId);
        cartService.delete(userId);
        logger.info("[Checkout] Cart finalized succesfully. userId='{}'", userId);
        return response;
    }

    private List<CheckoutItemDTO> buildCheckoutItems(Cart cart) {
        return cart.getItems()
                .values()
                .stream()
                .map(this::toCheckoutItem)
                .toList();
    }

    private CheckoutItemDTO toCheckoutItem(CartItem item) {
        ProductSnapshotDTO product = productGateway.findById(item.getProductId());
        checkoutValidator.validateProductForCheckout(product, item.getQuantity());
        return new CheckoutItemDTO(
                product.id(),
                item.getQuantity(),
                product.price()
        );
    }
}
