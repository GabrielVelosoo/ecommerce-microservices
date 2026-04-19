package io.github.gabrielvelosoo.authservice.domain.repository;

import io.github.gabrielvelosoo.authservice.application.dto.register.RegisterUserCommand;

public interface IdentityProviderRepository {

    boolean existsByEmail(String email);
    void createUser(RegisterUserCommand command);
}
