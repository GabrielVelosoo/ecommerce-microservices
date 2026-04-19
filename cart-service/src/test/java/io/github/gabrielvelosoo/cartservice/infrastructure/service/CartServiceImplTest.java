package io.github.gabrielvelosoo.cartservice.infrastructure.service;

import io.github.gabrielvelosoo.cartservice.domain.entity.Cart;
import io.github.gabrielvelosoo.cartservice.domain.repository.CartRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CartServiceImplTest {

    @Mock
    CartRepository cartRepository;

    @InjectMocks
    CartServiceImpl cartService;

    @Nested
    class GetOrInitializeTests {

        @Test
        void shouldReturnExistingCartWhenFound() {
            Cart existingCart = new Cart("user-123");
            when(cartRepository.getCart(existingCart.getUserId())).thenReturn(existingCart);
            Cart result = cartService.getOrInitialize(existingCart.getUserId());
            assertNotNull(result);
            assertSame(existingCart, result);
            assertEquals("user-123", result.getUserId());
        }

        @Test
        void shouldInitializeNewCartWhenNotFound() {
            String userId = "user-123";
            when(cartRepository.getCart(userId)).thenReturn(null);
            Cart result = cartService.getOrInitialize(userId);
            assertNotNull(result);
            assertEquals("user-123", result.getUserId());
        }
    }
}
