package io.github.gabrielvelosoo.customerservice.unit.application.validator.custom;

import io.github.gabrielvelosoo.customerservice.application.validator.custom.AddressValidator;
import io.github.gabrielvelosoo.customerservice.domain.entity.Address;
import io.github.gabrielvelosoo.customerservice.domain.entity.Customer;
import io.github.gabrielvelosoo.customerservice.domain.service.AuthService;
import io.github.gabrielvelosoo.customerservice.application.exception.BusinessRuleException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddressValidatorTest {

    @Mock
    AuthService authService;

    @InjectMocks
    AddressValidator addressValidator;

    @Nested
    class ValidateOnUpdateAndDeleteTests {

        @Test
        void shouldThrowExceptionWhenLoggedCustomerIsNull() {
            Address address = new Address();
            when(authService.getLoggedCustomer()).thenReturn(null);
            BusinessRuleException exception = assertThrows(
                    BusinessRuleException.class,
                    () -> addressValidator.validateOnUpdateAndDelete(address)
            );
            assertEquals(
                    "You don’t have permission to manage this address",
                    exception.getMessage()
            );
        }

        @Test
        void shouldThrowExceptionWhenAddressDoesNotBelongToLoggedCustomer() {
            Address address = new Address();
            Customer customer = new Customer();
            when(authService.getLoggedCustomer()).thenReturn(customer);
            BusinessRuleException exception = assertThrows(
                    BusinessRuleException.class,
                    () -> addressValidator.validateOnUpdateAndDelete(address)
            );
            assertEquals(
                    "You don’t have permission to manage this address",
                    exception.getMessage()
            );
            verify(authService).getLoggedCustomer();
        }

        @Test
        void shouldPassValidationWhenAddressBelongsToLoggedCustomer() {
            Customer customer = new Customer();
            customer.setId(1L);
            Address address = new Address();
            address.setId(10L);
            address.setCustomer(customer);
            when(authService.getLoggedCustomer()).thenReturn(customer);
            assertDoesNotThrow(
                    () -> addressValidator.validateOnUpdateAndDelete(address)
            );
            verify(authService).getLoggedCustomer();
        }

        @Test
        void shouldThrowExceptionWhenAddressHasNoCustomer() {
            Address address = new Address();
            Customer customer = new Customer();
            address.setCustomer(null);
            when(authService.getLoggedCustomer()).thenReturn(customer);
            BusinessRuleException exception = assertThrows(
                    BusinessRuleException.class,
                    () -> addressValidator.validateOnUpdateAndDelete(address)
            );
            assertEquals(
                    "You don’t have permission to manage this address",
                    exception.getMessage()
            );
        }
    }
}