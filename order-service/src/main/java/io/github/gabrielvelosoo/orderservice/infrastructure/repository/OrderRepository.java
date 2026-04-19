package io.github.gabrielvelosoo.orderservice.infrastructure.repository;

import io.github.gabrielvelosoo.orderservice.domain.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
