package io.github.gabrielvelosoo.authservice.infrastructure.keycloak;

import io.github.gabrielvelosoo.authservice.infrastructure.exception.KeycloakException;
import jakarta.ws.rs.core.Response;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class KeycloakClient {

    private final Keycloak keycloak;

    public KeycloakClient(Keycloak keycloak) {
        this.keycloak = keycloak;
    }

    public boolean userExistsByEmail(String realm, String email) {
        List<UserRepresentation> users = keycloak
                .realm(realm)
                .users()
                .searchByEmail(email, true);
        return users.stream()
                .anyMatch(user -> email.equalsIgnoreCase(user.getEmail()));
    }

    public void createUser(
            String realm,
            String email,
            String firstName,
            String lastName,
            String password
    ) {
        UserRepresentation user = new UserRepresentation();
        user.setUsername(email);
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEnabled(true);
        user.setEmailVerified(true);
        Response response = keycloak
                .realm(realm)
                .users()
                .create(user);
        if(response.getStatus() != 201) {
            throw new KeycloakException("Failed to create user in Keycloak");
        }
        String userId = CreatedResponseUtil.getCreatedId(response);
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(password);
        credential.setTemporary(false);
        keycloak.realm(realm)
                .users()
                .get(userId)
                .resetPassword(credential);
    }
}
