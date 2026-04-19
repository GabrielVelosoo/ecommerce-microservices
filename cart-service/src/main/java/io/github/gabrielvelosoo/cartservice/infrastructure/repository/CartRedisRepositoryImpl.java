package io.github.gabrielvelosoo.cartservice.infrastructure.repository;

import io.github.gabrielvelosoo.cartservice.domain.entity.Cart;
import io.github.gabrielvelosoo.cartservice.domain.repository.CartRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
public class CartRedisRepositoryImpl implements CartRepository {

    private final RedisTemplate<String, Cart> redisTemplate;

    public CartRedisRepositoryImpl(RedisTemplate<String, Cart> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Cart getCart(String userId) {
        return redisTemplate.opsForValue().get(key(userId));
    }

    @Override
    public void save(Cart cart) {
        redisTemplate.opsForValue().set(key(cart.getUserId()), cart, Duration.ofHours(1));
    }

    @Override
    public void delete(String userId) {
        redisTemplate.delete(key(userId));
    }

    private String key(String userId) {
        return "cart:" + userId;
    }
}
