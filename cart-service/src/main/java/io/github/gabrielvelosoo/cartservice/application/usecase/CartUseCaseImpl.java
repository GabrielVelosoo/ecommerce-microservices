package io.github.gabrielvelosoo.cartservice.application.usecase;

import io.github.gabrielvelosoo.cartservice.application.dto.cart.CartItemRequestDTO;
import io.github.gabrielvelosoo.cartservice.application.dto.cart.CartResponseDTO;
import io.github.gabrielvelosoo.cartservice.application.dto.product.ProductSnapshotDTO;
import io.github.gabrielvelosoo.cartservice.application.gateway.ProductGateway;
import io.github.gabrielvelosoo.cartservice.application.mapper.CartMapper;
import io.github.gabrielvelosoo.cartservice.application.validator.custom.ProductValidator;
import io.github.gabrielvelosoo.cartservice.domain.entity.Cart;
import io.github.gabrielvelosoo.cartservice.domain.service.AuthService;
import io.github.gabrielvelosoo.cartservice.domain.service.CartService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class CartUseCaseImpl implements CartUseCase {

    private static final Logger logger = LogManager.getLogger(CartUseCaseImpl.class);

    private final CartService cartService;
    private final CartMapper cartMapper;
    private final AuthService authService;
    private final ProductGateway productGateway;
    private final ProductValidator productValidator;

    public CartUseCaseImpl(CartService cartService, CartMapper cartMapper, AuthService authService, ProductGateway productGateway, ProductValidator productValidator) {
        this.cartService = cartService;
        this.cartMapper = cartMapper;
        this.authService = authService;
        this.productGateway = productGateway;
        this.productValidator = productValidator;
    }

    @Override
    public CartResponseDTO getCart() {
        String userId = authService.getUserId();
        logger.debug("[GetCart] Starting get cart for userId='{}'", userId);
        Cart cart = cartService.getOrInitialize(userId);
        logger.debug("[GetCart] Cart retrieved successfully for userId='{}'", userId);
        return cartMapper.toDTO(cart);
    }

    @Override
    public CartResponseDTO addItem(CartItemRequestDTO request) {
        String userId = authService.getUserId();
        logger.debug("[AddItem] Adding productId='{}' quantity='{}' to cart userId='{}'",
                request.productId(), request.quantity(), userId);
        ProductSnapshotDTO product = productGateway.findById(request.productId());
        productValidator.validateOnAddItem(product, request.quantity());
        Cart cart = cartService.getOrInitialize(userId);
        cart.addItem(request.productId(), request.quantity());
        cartService.save(cart);
        logger.debug("[AddItem] ProductId='{}' added successfully to cart userId='{}'",
                request.productId(), userId);
        return cartMapper.toDTO(cart);
    }

    @Override
    public CartResponseDTO updateItem(CartItemRequestDTO request) {
        String userId = authService.getUserId();
        logger.debug("[UpdateItem] Updating productId='{}' quantity='{}' in cart userId='{}'",
                request.productId(), request.quantity(), userId);
        ProductSnapshotDTO product = productGateway.findById(request.productId());
        productValidator.validateOnUpdateItem(product, request.quantity());
        Cart cart = cartService.getOrInitialize(userId);
        cart.updateItem(request.productId(), request.quantity());
        persistCartOrDeleteIfEmpty(cart);
        logger.debug("[UpdateItem] ProductId='{}' updated successfully in cart userId='{}'",
                request.productId(), userId);
        return cartMapper.toDTO(cart);
    }

    @Override
    public CartResponseDTO removeItem(Long productId) {
        String userId = authService.getUserId();
        logger.debug("[RemoveItem] Removing productId='{}' from cart userId='{}'", productId, userId);
        Cart cart = cartService.getOrInitialize(userId);
        cart.removeItem(productId);
        persistCartOrDeleteIfEmpty(cart);
        logger.debug("[RemoveItem] ProductId='{}' removed successfully from cart userId='{}'",
                productId, userId);
        return cartMapper.toDTO(cart);
    }

    private void persistCartOrDeleteIfEmpty(Cart cart) {
        if(cart.isEmpty()) {
            logger.debug("[PersistCart] Cart is empty, deleting cart for userId='{}'", cart.getUserId());
            cartService.delete(cart.getUserId());
        } else {
            logger.debug("[PersistCart] Saving cart for userId='{}'", cart.getUserId());
            cartService.save(cart);
        }
    }
}
