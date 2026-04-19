package io.github.gabrielvelosoo.cartservice.application.validator.custom;

import io.github.gabrielvelosoo.cartservice.application.dto.product.ProductSnapshotDTO;
import io.github.gabrielvelosoo.cartservice.application.exception.BusinessException;
import io.github.gabrielvelosoo.cartservice.application.exception.RecordNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ProductValidatorTest {

    ProductValidator productValidator;

    @BeforeEach
    void setUp() {
        productValidator = new ProductValidator();
    }

    @Nested
    class ValidateOnAddItemTests {

        @Test
        void shouldThrowExceptionWhenProductIsNull() {
            RecordNotFoundException e = assertThrows(
                    RecordNotFoundException.class,
                    () -> productValidator.validateOnAddItem(null, 1)
            );
            assertEquals("Product not found", e.getMessage());
        }

        @Test
        void shouldThrowExceptionWhenProductIsOutOfStock() {
            ProductSnapshotDTO product = new ProductSnapshotDTO(
                    1L, BigDecimal.TEN, 0
            );
            BusinessException e = assertThrows(
                    BusinessException.class,
                    () -> productValidator.validateOnAddItem(product, 1)
            );
            assertEquals("Product out of stock", e.getMessage());
        }

        @Test
        void shouldThrowExceptionWhenRequestedQuantityExceedsStock() {
            ProductSnapshotDTO product = new ProductSnapshotDTO(
                    1L, BigDecimal.TEN, 5
            );
            BusinessException e = assertThrows(
                    BusinessException.class,
                    () -> productValidator.validateOnAddItem(product, 10)
            );
            assertEquals("Requested quantity exceeds stock", e.getMessage());
        }

        @Test
        void shouldNotThrowExceptionWhenProductAndQuantityAreValid() {
            ProductSnapshotDTO product = new ProductSnapshotDTO(
                    1L, BigDecimal.TEN, 10
            );
            assertDoesNotThrow(() ->
                    productValidator.validateOnAddItem(product, 5)
            );
        }
    }

    @Nested
    class ValidateOnUpdateItemTests {

        @Test
        void shouldThrowExceptionWhenProductIsNull() {
            RecordNotFoundException e = assertThrows(
                    RecordNotFoundException.class,
                    () -> productValidator.validateOnUpdateItem(null, 1)
            );
            assertEquals("Product not found", e.getMessage());
        }

        @Test
        void shouldThrowExceptionWhenProductIsOutOfStock() {
            ProductSnapshotDTO product = new ProductSnapshotDTO(
                    1L, BigDecimal.TEN, 0
            );
            BusinessException e = assertThrows(
                    BusinessException.class,
                    () -> productValidator.validateOnUpdateItem(product, 1)
            );
            assertEquals("Product out of stock", e.getMessage());
        }

        @Test
        void shouldThrowExceptionWhenRequestedQuantityExceedsStock() {
            ProductSnapshotDTO product = new ProductSnapshotDTO(
                    1L, BigDecimal.TEN, 5
            );
            BusinessException e = assertThrows(
                    BusinessException.class,
                    () -> productValidator.validateOnUpdateItem(product, 10)
            );
            assertEquals("Requested quantity exceeds stock", e.getMessage());
        }

        @Test
        void shouldNotThrowExceptionWhenProductAndQuantityAreValid() {
            ProductSnapshotDTO product = new ProductSnapshotDTO(
                    1L, BigDecimal.TEN, 10
            );
            assertDoesNotThrow(() ->
                    productValidator.validateOnUpdateItem(product, 5)
            );
        }
    }
}