package io.github.gabrielvelosoo.authservice.infrastructure.keycloak;

import io.github.gabrielvelosoo.authservice.domain.repository.IdentityProviderRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
public class KeycloakUserRepository implements IdentityProviderRepository {

    private final KeycloakClient keycloakClient;
    private final String realm;

    public KeycloakUserRepository(
            KeycloakClient keycloakClient,
            @Value("${keycloak.realm}") String realm
    ) {
        this.keycloakClient = keycloakClient;
        this.realm = realm;
    }

    @Override
    public boolean existsByEmail(String email) {
        return keycloakClient.userExistsByEmail(realm, email);
    }
}
