package io.github.gabrielvelosoo.customerservice.unit.infrastructure.messaging.consumer;

import io.github.gabrielvelosoo.customerservice.application.dto.event.CustomerCreatedEvent;
import io.github.gabrielvelosoo.customerservice.application.dto.event.CustomerDeletedEvent;
import io.github.gabrielvelosoo.customerservice.application.dto.event.CustomerUpdatedEvent;
import io.github.gabrielvelosoo.customerservice.domain.entity.Customer;
import io.github.gabrielvelosoo.customerservice.domain.service.CustomerService;
import io.github.gabrielvelosoo.customerservice.domain.service.IdentityProvider;
import io.github.gabrielvelosoo.customerservice.infrastructure.messaging.consumer.KeycloakConsumer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KeycloakConsumerTest {

    @Mock
    IdentityProvider identityProvider;

    @Mock
    CustomerService customerService;

    @InjectMocks
    KeycloakConsumer consumer;

    @Test
    void handleCustomerCreated_shouldCreateKeycloakUserAndSaveCustomer_whenCustomerHasNoKeycloakId() {
        CustomerCreatedEvent event = new CustomerCreatedEvent(1L, "Gabriel", "Veloso", "g@mail", "pwd");
        Customer customer = new Customer();
        customer.setId(1L);
        when(customerService.findById(1L)).thenReturn(customer);
        when(identityProvider.createUser("g@mail", "pwd", "Gabriel", "Veloso")).thenReturn("kc-123");
        consumer.handleCustomerCreated(event);
        assertEquals("kc-123", customer.getKeycloakUserId());
        verify(identityProvider).createUser("g@mail", "pwd", "Gabriel", "Veloso");
        verify(customerService).save(customer);
    }

    @Test
    void handleCustomerCreated_shouldSkipWhenCustomerAlreadyHasKeycloakId() {
        CustomerCreatedEvent event = new CustomerCreatedEvent(1L, "Gabriel", "Veloso", "g@mail", "pwd");
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setKeycloakUserId("existing");
        when(customerService.findById(1L)).thenReturn(customer);
        consumer.handleCustomerCreated(event);
        verify(identityProvider, never()).createUser(any(), any(), any(), any());
        verify(customerService, never()).save(any());
    }

    @Test
    void handleCustomerCreated_shouldPropagateExceptionWhenIdentityProviderFails() {
        CustomerCreatedEvent event = new CustomerCreatedEvent(1L, "Gabriel", "Veloso", "g@mail", "pwd");
        Customer customer = new Customer();
        customer.setId(1L);
        when(customerService.findById(1L)).thenReturn(customer);
        when(identityProvider.createUser(any(), any(), any(), any())).thenThrow(new RuntimeException("KC down"));
        RuntimeException ex = assertThrows(RuntimeException.class, () -> consumer.handleCustomerCreated(event));
        assertEquals("KC down", ex.getMessage());
        verify(customerService, never()).save(any());
    }

    @Test
    void handleCustomerUpdated_shouldEditWhenKeycloakIdExists() {
        CustomerUpdatedEvent event = new CustomerUpdatedEvent(2L, "New", "Name");
        Customer customer = new Customer();
        customer.setId(2L);
        customer.setKeycloakUserId("kc-42");
        when(customerService.findById(2L)).thenReturn(customer);
        consumer.handleCustomerUpdated(event);
        verify(identityProvider).editUser("kc-42", "New", "Name");
    }

    @Test
    void handleCustomerUpdated_shouldSkipWhenNoKeycloakId() {
        CustomerUpdatedEvent event = new CustomerUpdatedEvent(2L, "New", "Name");
        Customer customer = new Customer();
        customer.setId(2L);
        when(customerService.findById(2L)).thenReturn(customer);
        consumer.handleCustomerUpdated(event);
        verify(identityProvider, never()).editUser(anyString(), anyString(), anyString());
    }

    @Test
    void handleCustomerDeleted_shouldDeleteWhenKeycloakIdExists() {
        CustomerDeletedEvent event = new CustomerDeletedEvent(3L);
        Customer customer = new Customer();
        customer.setId(3L);
        customer.setKeycloakUserId("kc-delete");
        when(customerService.findById(3L)).thenReturn(customer);
        consumer.handleCustomerDeleted(event);
        verify(identityProvider).deleteUser("kc-delete");
    }

    @Test
    void handleCustomerDeleted_shouldSkipWhenNoKeycloakId() {
        CustomerDeletedEvent event = new CustomerDeletedEvent(3L);
        Customer customer = new Customer();
        customer.setId(3L);
        when(customerService.findById(3L)).thenReturn(customer);
        consumer.handleCustomerDeleted(event);
        verify(identityProvider, never()).deleteUser(anyString());
    }

    @Test
    void handleCustomerDeleted_shouldPropagateExceptionWhenIdentityProviderFails() {
        CustomerDeletedEvent event = new CustomerDeletedEvent(3L);
        Customer customer = new Customer();
        customer.setId(3L);
        customer.setKeycloakUserId("kc-delete");
        when(customerService.findById(3L)).thenReturn(customer);
        doThrow(new RuntimeException("KC fail")).when(identityProvider).deleteUser("kc-delete");
        RuntimeException ex = assertThrows(RuntimeException.class, () -> consumer.handleCustomerDeleted(event));
        assertEquals("KC fail", ex.getMessage());
        verify(identityProvider).deleteUser("kc-delete");
    }
}