package io.github.gabrielvelosoo.cartservice.domain.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.gabrielvelosoo.cartservice.application.exception.BusinessException;
import io.github.gabrielvelosoo.cartservice.application.exception.RecordNotFoundException;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class Cart {

    private String userId;
    private Map<Long, CartItem> items = new HashMap<>();

    public Cart(String userId) {
        this.userId = userId;
    }

    @JsonCreator
    public Cart(
            @JsonProperty("userId") String userId,
            @JsonProperty("items") Map<Long, CartItem> items
    ) {
        this.userId = userId;
        this.items = items != null ? items : new HashMap<>();
    }

    public void addItem(Long productId, Integer quantity) {
        if(quantity <= 0) {
            throw new BusinessException("Quantity must be greater than zero");
        }
        CartItem item = items.get(productId);
        if(item == null) {
            items.put(productId, new CartItem(productId, quantity));
        } else {
            item.increaseQuantity(quantity);
        }
    }

    public void updateItem(Long productId, Integer quantity) {
        if(quantity <= 0) {
            items.remove(productId);
            return;
        }
        CartItem item = items.get(productId);
        if(item == null) {
            throw new RecordNotFoundException("Item not found in cart");
        }
        item.setQuantity(quantity);
    }

    public void removeItem(Long productId) {
        if(!items.containsKey(productId)) {
            throw new RecordNotFoundException("Item not found in cart");
        }
        items.remove(productId);
    }

    @JsonIgnore
    public boolean isEmpty() {
        return items.isEmpty();
    }
}
