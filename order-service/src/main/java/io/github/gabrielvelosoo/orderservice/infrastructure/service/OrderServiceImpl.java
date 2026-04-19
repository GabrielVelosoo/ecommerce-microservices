package io.github.gabrielvelosoo.orderservice.infrastructure.service;

import io.github.gabrielvelosoo.orderservice.application.exception.RecordNotFoundException;
import io.github.gabrielvelosoo.orderservice.domain.entity.Order;
import io.github.gabrielvelosoo.orderservice.domain.service.OrderService;
import io.github.gabrielvelosoo.orderservice.infrastructure.repository.OrderRepository;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    public OrderServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public Order save(Order order) {
        return orderRepository.save(order);
    }

    @Override
    public Order findById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow( () -> new RecordNotFoundException("Order not found") );
    }
}
