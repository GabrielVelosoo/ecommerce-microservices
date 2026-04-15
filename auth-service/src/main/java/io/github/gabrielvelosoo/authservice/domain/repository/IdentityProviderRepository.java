package io.github.gabrielvelosoo.authservice.domain.repository;

public interface IdentityProviderRepository {

    boolean existsByEmail(String email);
}
