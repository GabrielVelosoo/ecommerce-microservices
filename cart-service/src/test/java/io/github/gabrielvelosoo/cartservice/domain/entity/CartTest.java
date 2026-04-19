package io.github.gabrielvelosoo.cartservice.domain.entity;

import io.github.gabrielvelosoo.cartservice.application.exception.BusinessException;
import io.github.gabrielvelosoo.cartservice.application.exception.RecordNotFoundException;
import io.github.gabrielvelosoo.cartservice.domain.entity.Cart;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CartTest {

    Cart cart;

    @BeforeEach
    void setUp() {
        cart = new Cart("user-123");
    }

    @Nested
    class AddItemTests {

        @Test
        void shouldAddNewItemWhenProductNotExists() {
            cart.addItem(1L, 2);
            assertEquals(1, cart.getItems().size());
            assertEquals(2, cart.getItems().get(1L).getQuantity());
        }

        @Test
        void shouldIncreaseQuantityWhenItemAlreadyExists() {
            cart.addItem(1L, 2);
            cart.addItem(1L, 3);
            assertEquals(1, cart.getItems().size());
            assertEquals(5, cart.getItems().get(1L).getQuantity());
        }

        @Test
        void shouldThrowExceptionWhenAddItemWithZeroOrNegativeQuantity() {
            BusinessException e1 = assertThrows(
                    BusinessException.class,
                    () -> cart.addItem(1L, 0)
            );
            BusinessException e2 = assertThrows(
                    BusinessException.class,
                    () -> cart.addItem(1L, -1)
            );
            assertEquals("Quantity must be greater than zero", e1.getMessage());
            assertEquals("Quantity must be greater than zero", e2.getMessage());
        }

        @Test
        void shouldHandleMultipleItemsIndependently() {
            cart.addItem(1L, 2);
            cart.addItem(2L, 3);

            assertEquals(2, cart.getItems().size());
            assertEquals(2, cart.getItems().get(1L).getQuantity());
            assertEquals(3, cart.getItems().get(2L).getQuantity());
        }
    }

    @Nested
    class UpdateItemTests {

        @Test
        void shouldUpdateItemQuantity() {
            cart.addItem(1L, 2);
            cart.updateItem(1L, 5);
            assertEquals(5, cart.getItems().get(1L).getQuantity());
        }

        @Test
        void shouldRemoveItemWhenUpdateWithZeroOrNegativeQuantity() {
            cart.addItem(1L, 2);
            cart.updateItem(1L, 0);
            assertTrue(cart.getItems().isEmpty());
        }

        @Test
        void shouldThrowExceptionWhenUpdatingNonExistingItem() {
            RecordNotFoundException e = assertThrows(
                    RecordNotFoundException.class,
                    () -> cart.updateItem(1L, 3)
            );
            assertEquals("Item not found in cart", e.getMessage());
        }
    }

    @Nested
    class RemoveItemTests {

        @Test
        void shouldRemoveItem() {
            cart.addItem(1L, 2);
            cart.removeItem(1L);
            assertTrue(cart.getItems().isEmpty());
        }

        @Test
        void shouldThrowExceptionWhenRemovingNonExistingItem() {
            RecordNotFoundException e = assertThrows(
                    RecordNotFoundException.class,
                    () -> cart.removeItem(1L)
            );
            assertEquals("Item not found in cart", e.getMessage());
        }
    }

    @Nested
    class IsEmptyTests {

        @Test
        void shouldReturnTrueWhenCartIsEmpty() {
            assertTrue(cart.isEmpty());
        }

        @Test
        void shouldReturnFalseWhenCartHasItems() {
            cart.addItem(1L, 1);
            assertFalse(cart.isEmpty());
        }
    }
}
