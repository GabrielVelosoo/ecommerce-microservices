package io.github.gabrielvelosoo.cartservice.application.usecase;

import io.github.gabrielvelosoo.cartservice.application.dto.cart.CartResponseDTO;
import io.github.gabrielvelosoo.cartservice.application.dto.cart.CartItemRequestDTO;
import io.github.gabrielvelosoo.cartservice.application.dto.product.ProductSnapshotDTO;
import io.github.gabrielvelosoo.cartservice.application.gateway.ProductGateway;
import io.github.gabrielvelosoo.cartservice.application.mapper.CartMapper;
import io.github.gabrielvelosoo.cartservice.application.validator.custom.ProductValidator;
import io.github.gabrielvelosoo.cartservice.domain.entity.Cart;
import io.github.gabrielvelosoo.cartservice.domain.service.AuthService;
import io.github.gabrielvelosoo.cartservice.domain.service.CartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartUseCaseImplTest {

    @Mock
    CartService cartService;

    @Mock
    CartMapper cartMapper;

    @Mock
    AuthService authService;

    @Mock
    ProductGateway productGateway;

    @Mock
    ProductValidator productValidator;

    @InjectMocks
    CartUseCaseImpl cartUseCase;

    Cart cart;

    private final String USER_ID = "user-123";

    @BeforeEach
    void setUp() {
        cart = new Cart(USER_ID);
    }

    @Nested
    class GetCartTests {

        @Test
        void shouldReturnCartWhenGetCart() {
            CartResponseDTO responseDTO = mock(CartResponseDTO.class);
            when(authService.getUserId()).thenReturn(USER_ID);
            when(cartService.getOrInitialize(USER_ID)).thenReturn(cart);
            when(cartMapper.toDTO(cart)).thenReturn(responseDTO);
            CartResponseDTO result = cartUseCase.getCart();
            assertNotNull(result);
            verify(authService, times(1)).getUserId();
            verify(cartService, times(1)).getOrInitialize(USER_ID);
            verify(cartMapper, times(1)).toDTO(cart);
        }
    }

    @Nested
    class AddItemTests {

        @Test
        void shouldAddItemAndSaveCart() {
            CartItemRequestDTO requestDTO = new CartItemRequestDTO(
                    1L, 2
            );
            ProductSnapshotDTO product = new ProductSnapshotDTO(
                    1L, BigDecimal.TEN, 10
            );
            CartResponseDTO responseDTO = mock(CartResponseDTO.class);
            when(authService.getUserId()).thenReturn(USER_ID);
            when(productGateway.findById(1L)).thenReturn(product);
            when(cartService.getOrInitialize(USER_ID)).thenReturn(cart);
            when(cartMapper.toDTO(cart)).thenReturn(responseDTO);
            CartResponseDTO result = cartUseCase.addItem(requestDTO);
            assertNotNull(result);
            verify(authService, times(1)).getUserId();
            verify(productGateway, times(1)).findById(1L);
            verify(productValidator, times(1)).validateOnAddItem(product, 2);
            verify(cartService, times(1)).getOrInitialize(USER_ID);
            verify(cartService, times(1)).save(cart);
            verify(cartMapper, times(1)).toDTO(cart);
        }
    }

    @Nested
    class UpdateItemTests {

        @Test
        void shouldUpdateItemAndSaveCartWhenNotEmpty() {
            CartItemRequestDTO requestDTO = new CartItemRequestDTO(
                    1L, 3
            );
            ProductSnapshotDTO product = new ProductSnapshotDTO(
                    1L, BigDecimal.TEN, 10
            );
            cart.addItem(1L, 1);
            CartResponseDTO responseDTO = mock(CartResponseDTO.class);
            when(authService.getUserId()).thenReturn(USER_ID);
            when(productGateway.findById(1L)).thenReturn(product);
            when(cartService.getOrInitialize(USER_ID)).thenReturn(cart);
            when(cartMapper.toDTO(cart)).thenReturn(responseDTO);
            CartResponseDTO result = cartUseCase.updateItem(requestDTO);
            assertNotNull(result);
            verify(productValidator, times(1)).validateOnUpdateItem(product, 3);
            verify(cartService, times(1)).save(cart);
        }

        @Test
        void shouldDeleteCartWhenUpdateResultsInEmptyCart() {
            CartItemRequestDTO requestDTO = new CartItemRequestDTO(
                    1L, 0
            );
            ProductSnapshotDTO product = new ProductSnapshotDTO(
                    1L, BigDecimal.TEN, 10
            );
            cart.addItem(1L, 1);
            when(authService.getUserId()).thenReturn(USER_ID);
            when(productGateway.findById(1L)).thenReturn(product);
            when(cartService.getOrInitialize(USER_ID)).thenReturn(cart);
            when(cartMapper.toDTO(cart)).thenReturn(mock(CartResponseDTO.class));
            cartUseCase.updateItem(requestDTO);
            verify(cartService, times(1)).delete(USER_ID);
        }
    }

    @Nested
    class RemoveItemTests {

        @Test
        void shouldRemoveItemAndDeleteCartIfEmpty() {
            cart.addItem(1L, 1);
            when(authService.getUserId()).thenReturn(USER_ID);
            when(cartService.getOrInitialize(USER_ID)).thenReturn(cart);
            when(cartMapper.toDTO(cart)).thenReturn(mock(CartResponseDTO.class));
            cartUseCase.removeItem(1L);
            verify(cartService, times(1)).delete(USER_ID);
        }
    }
}