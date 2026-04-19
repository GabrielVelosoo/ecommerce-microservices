package io.github.gabrielvelosoo.orderservice.domain.entity;

import io.github.gabrielvelosoo.orderservice.application.exception.BusinessException;
import jakarta.persistence.*;
import lombok.Getter;

import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "tb_order_item")
@Getter
public class OrderItem implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal price;

    protected OrderItem() {}

    public OrderItem(Order order, Long productId, Integer quantity, BigDecimal price) {
        if(quantity <= 0) {
            throw new BusinessException("Quantity must be greater than zero");
        }
        this.order = order;
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
    }

    public BigDecimal getSubtotal() {
        return price.multiply(BigDecimal.valueOf(quantity));
    }
}
