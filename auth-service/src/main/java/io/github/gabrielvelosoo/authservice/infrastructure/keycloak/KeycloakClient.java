package io.github.gabrielvelosoo.authservice.infrastructure.keycloak;

import org.keycloak.admin.client.Keycloak;
import org.springframework.stereotype.Component;

@Component
public class KeycloakClient {

    private final Keycloak keycloak;

    public KeycloakClient(Keycloak keycloak) {
        this.keycloak = keycloak;
    }

    public boolean userExistsByEmail(String realm, String email) {
        return !keycloak
                .realm(realm)
                .users()
                .searchByEmail(email, true)
                .isEmpty();
    }
}
