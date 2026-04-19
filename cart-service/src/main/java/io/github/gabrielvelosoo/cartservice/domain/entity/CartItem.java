package io.github.gabrielvelosoo.cartservice.domain.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.gabrielvelosoo.cartservice.application.exception.BusinessException;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartItem {

    private final Long productId;
    private Integer quantity;

    @JsonCreator
    public CartItem(
            @JsonProperty("productId") Long productId,
            @JsonProperty("quantity") Integer quantity
    ) {
        this.productId = productId;
        this.quantity = quantity;
    }

    public void increaseQuantity(Integer amount) {
        if(amount <= 0) {
            throw new BusinessException("Amount must be positive");
        }
        this.quantity += amount;
    }
}
