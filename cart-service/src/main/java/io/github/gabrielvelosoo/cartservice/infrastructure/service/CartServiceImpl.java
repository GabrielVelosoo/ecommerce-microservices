package io.github.gabrielvelosoo.cartservice.infrastructure.service;

import io.github.gabrielvelosoo.cartservice.domain.entity.Cart;
import io.github.gabrielvelosoo.cartservice.domain.repository.CartRepository;
import io.github.gabrielvelosoo.cartservice.domain.service.CartService;
import org.springframework.stereotype.Service;

@Service
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;

    public CartServiceImpl(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    @Override
    public Cart getOrInitialize(String userId) {
        Cart cart = cartRepository.getCart(userId);
        return cart != null ? cart : new Cart(userId);
    }

    @Override
    public void save(Cart cart) {
        cartRepository.save(cart);
    }

    @Override
    public void delete(String userId) {
        cartRepository.delete(userId);
    }
}
