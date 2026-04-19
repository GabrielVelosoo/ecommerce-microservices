package io.github.gabrielvelosoo.authservice.infrastructure.keycloak;

import io.github.gabrielvelosoo.authservice.application.dto.register.RegisterUserCommand;
import io.github.gabrielvelosoo.authservice.domain.repository.IdentityProviderRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
public class KeycloakIdentityProviderRepository implements IdentityProviderRepository {

    private final KeycloakClient keycloakClient;
    private final String realm;

    public KeycloakIdentityProviderRepository(
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

    @Override
    public void createUser(RegisterUserCommand command) {
        keycloakClient.createUser(
                realm,
                command.email(),
                command.firstName(),
                command.lastName(),
                command.password()
        );
    }
}
