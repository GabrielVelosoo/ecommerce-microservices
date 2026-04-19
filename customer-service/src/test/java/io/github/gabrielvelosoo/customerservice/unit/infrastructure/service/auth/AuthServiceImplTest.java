package io.github.gabrielvelosoo.customerservice.unit.infrastructure.service.auth;

import io.github.gabrielvelosoo.customerservice.domain.entity.Customer;
import io.github.gabrielvelosoo.customerservice.domain.service.CustomerService;
import io.github.gabrielvelosoo.customerservice.infrastructure.service.auth.AuthServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    CustomerService customerService;

    @InjectMocks
    AuthServiceImpl authService;

    @Test
    void shouldReturnLoggedCustomer() {
        String keycloakUserId = "keycloak-123";
        Jwt jwt = mock(Jwt.class);
        when(jwt.getSubject()).thenReturn(keycloakUserId);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(jwt);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        Customer customer = new Customer();
        customer.setId(1L);
        when(customerService.findByKeycloakUserId(keycloakUserId)).thenReturn(customer);
        Customer result = authService.getLoggedCustomer();
        assertEquals(customer, result);
        verify(customerService).findByKeycloakUserId(keycloakUserId);
    }
}