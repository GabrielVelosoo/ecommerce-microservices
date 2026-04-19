package io.github.gabrielvelosoo.orderservice.application.validator.custom;

import io.github.gabrielvelosoo.orderservice.domain.entity.Order;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.stereotype.Component;

@Component
public class OrderValidator {

    public void validateOnRemoveItem(Order order, String userId) {
        validateUserOwnership(order, userId);
    }

    public void validateOnCancelOrder(Order order, String userId) {
        validateUserOwnership(order, userId);
    }

    private void validateUserOwnership(Order order, String userId) {
        if(!order.getUserId().equals(userId)) {
            throw new AuthorizationDeniedException("User not authorized to modify this order");
        }
    }
}
