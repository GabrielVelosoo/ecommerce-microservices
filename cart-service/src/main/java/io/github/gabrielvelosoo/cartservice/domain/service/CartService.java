package io.github.gabrielvelosoo.cartservice.domain.service;

import io.github.gabrielvelosoo.cartservice.domain.entity.Cart;

public interface CartService {

    Cart getOrInitialize(String userId);
    void save(Cart cart);
    void delete(String userId);
}
