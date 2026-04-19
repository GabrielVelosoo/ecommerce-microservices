package io.github.gabrielvelosoo.cartservice.domain.repository;

import io.github.gabrielvelosoo.cartservice.domain.entity.Cart;

public interface CartRepository {

    Cart getCart(String userId);
    void save(Cart cart);
    void delete(String userId);
}
