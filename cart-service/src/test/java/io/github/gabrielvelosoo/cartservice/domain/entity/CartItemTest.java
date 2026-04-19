package io.github.gabrielvelosoo.cartservice.domain.entity;

import io.github.gabrielvelosoo.cartservice.application.exception.BusinessException;
import io.github.gabrielvelosoo.cartservice.domain.entity.CartItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CartItemTest {

    CartItem item;

    @BeforeEach
    void setUp() {
        item = new CartItem(1L, 2);
    }

    @Nested
    class IncreaseQuantityTests {

        @Test
        void shouldIncreaseQuantity() {
            item.increaseQuantity(3);
            assertEquals(5, item.getQuantity());
        }

        @Test
        void shouldThrowExceptionWhenIncreaseWithZeroOrNegativeAmount() {
            BusinessException e1 = assertThrows(
                    BusinessException.class,
                    () -> item.increaseQuantity(0)
            );
            BusinessException e2 = assertThrows(
                    BusinessException.class,
                    () -> item.increaseQuantity(-1)
            );
            assertEquals("Amount must be positive", e1.getMessage());
            assertEquals("Amount must be positive", e2.getMessage());
        }
    }
}
