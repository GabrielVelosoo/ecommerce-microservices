package io.github.gabrielvelosoo.orderservice.domain.service;

import io.github.gabrielvelosoo.orderservice.domain.entity.Order;

public interface OrderService {

    Order save(Order order);
    Order findById(Long id);
}
