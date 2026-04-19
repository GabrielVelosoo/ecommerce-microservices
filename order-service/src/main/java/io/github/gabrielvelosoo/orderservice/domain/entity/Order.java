package io.github.gabrielvelosoo.orderservice.domain.entity;

import io.github.gabrielvelosoo.orderservice.application.exception.BusinessException;
import io.github.gabrielvelosoo.orderservice.application.exception.RecordNotFoundException;
import io.github.gabrielvelosoo.orderservice.domain.valueobject.OrderStatus;
import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tb_order")
@EntityListeners(AuditingEntityListener.class)
@Getter
public class Order implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, length = 100)
    private String userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private OrderStatus status;

    @OneToMany(
            mappedBy = "order",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<OrderItem> items = new ArrayList<>();

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal total;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Order() {}

    public static Order create(String userId) {
        Order order = new Order();
        order.userId = userId;
        order.status = OrderStatus.CREATED;
        order.total = BigDecimal.ZERO;
        return order;
    }

    public void addItem(Long productId, Integer quantity, BigDecimal price) {
        if(this.status != OrderStatus.CREATED) {
            throw new BusinessException("Cannot modify order in status " + status);
        }
        OrderItem item = new OrderItem(this, productId, quantity, price);
        this.items.add(item);
        recalculateTotal();
    }

    public OrderItem removeItem(Long productId) {
        if(this.status != OrderStatus.CREATED) {
            throw new BusinessException("Cannot modify order in status " + status);
        }
        OrderItem itemToRemove = items.stream()
                .filter(i -> i.getProductId().equals(productId))
                .findFirst()
                .orElseThrow( () -> new RecordNotFoundException("Item not found in order") );
        if(items.size() == 1) {
            throw new BusinessException("Cannot remove last item from order, cancel the order instead");
        }
        items.remove(itemToRemove);
        recalculateTotal();
        return itemToRemove;
    }

    private void recalculateTotal() {
        this.total = items.stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void cancel() {
        if(this.status == OrderStatus.PAID) {
            throw new BusinessException("Paid order cannot be canceled");
        }
        if(this.status == OrderStatus.CANCELED) {
            throw new BusinessException("Order is already canceled");
        }
        this.status = OrderStatus.CANCELED;
        this.cancelledAt = LocalDateTime.now();
    }
}
