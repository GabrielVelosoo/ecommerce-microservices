package io.github.gabrielvelosoo.customerservice.unit.infrastructure.service;

import io.github.gabrielvelosoo.customerservice.application.exception.RecordNotFoundException;
import io.github.gabrielvelosoo.customerservice.domain.entity.Customer;
import io.github.gabrielvelosoo.customerservice.infrastructure.persistence.repository.CustomerRepository;
import io.github.gabrielvelosoo.customerservice.infrastructure.service.CustomerServiceImpl;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {

    @Mock
    CustomerRepository customerRepository;

    @InjectMocks
    CustomerServiceImpl customerService;

    @Nested
    class FindByIdTests {

        @Test
        void shouldReturnAddressWhenExists() {
            Customer customer = new Customer();
            when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
            Customer result = customerService.findById(1L);
            assertEquals(customer, result);
            verify(customerRepository).findById(1L);
        }

        @Test
        void shouldThrowExceptionWhenAddressNotFound() {
            when(customerRepository.findById(1L)).thenReturn(Optional.empty());
            RecordNotFoundException e = assertThrows(
                    RecordNotFoundException.class,
                    () -> customerService.findById(1L)
            );
            assertEquals("Customer not found: 1", e.getMessage());
        }
    }

    @Nested
    class FindByKeycloakUserIdTests {

        @Test
        void shouldReturnCustomerWhenFoundByKeycloakUserId() {
            String keycloakUserId = "kc-123";
            Customer customer = new Customer();
            customer.setKeycloakUserId(keycloakUserId);
            when(customerRepository.findByKeycloakUserId(keycloakUserId))
                    .thenReturn(Optional.of(customer));
            Customer result = customerService.findByKeycloakUserId(keycloakUserId);
            assertNotNull(result);
            assertEquals(keycloakUserId, result.getKeycloakUserId());
            verify(customerRepository).findByKeycloakUserId(keycloakUserId);
        }

        @Test
        void shouldThrowExceptionWhenCustomerNotFoundByKeycloakUserId() {
            String keycloakUserId = "kc-not-exists";
            when(customerRepository.findByKeycloakUserId(keycloakUserId))
                    .thenReturn(Optional.empty());
            RecordNotFoundException e = assertThrows(
                    RecordNotFoundException.class,
                    () -> customerService.findByKeycloakUserId(keycloakUserId)
            );
            assertEquals(
                    "Customer with this keycloakUserId not found: " + keycloakUserId,
                    e.getMessage()
            );
            verify(customerRepository).findByKeycloakUserId(keycloakUserId);
        }
    }
}