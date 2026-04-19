package io.github.gabrielvelosoo.customerservice.infrastructure.service.auth;

import io.github.gabrielvelosoo.customerservice.domain.entity.Customer;
import io.github.gabrielvelosoo.customerservice.domain.service.AuthService;
import io.github.gabrielvelosoo.customerservice.domain.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final CustomerService customerService;

    @Override
    public Customer getLoggedCustomer() {
        String keycloakUserId = getLoggedKeycloakUserId();
        return customerService.findByKeycloakUserId(keycloakUserId);
    }

    private String getLoggedKeycloakUserId() {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return jwt.getSubject();
    }
}
