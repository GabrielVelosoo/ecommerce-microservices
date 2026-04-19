package io.github.gabrielvelosoo.cartservice.infrastructure.service.auth;

import io.github.gabrielvelosoo.cartservice.infrastructure.exception.KeycloakException;
import io.github.gabrielvelosoo.cartservice.application.dto.auth.TokenResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Instant;

@Component
public class KeycloakClientCredentialsTokenProvider {

    private static final int CLOCK_SKEW_SECONDS = 30;

    private final WebClient webClient;

    @Value("${keycloak.client-id}")
    private String clientId;

    @Value("${keycloak.client-secret}")
    private String clientSecret;

    private String cachedToken;
    private Instant expiresAt;

    public KeycloakClientCredentialsTokenProvider(
            WebClient.Builder builder,
            @Value("${keycloak.token-url}") String tokenUrl
    ) {
        this.webClient = builder.baseUrl(tokenUrl).build();
    }

    public synchronized String getToken() {
        if(cachedToken != null && expiresAt != null && Instant.now().isBefore(expiresAt)) {
            return cachedToken;
        }
        TokenResponse response;
        try {
            response = webClient.post()
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .bodyValue(
                            "grant_type=client_credentials" +
                                    "&client_id=" + clientId +
                                    "&client_secret=" + clientSecret
                    )
                    .retrieve()
                    .bodyToMono(TokenResponse.class)
                    .block();
        } catch(Exception e) {
            throw new KeycloakException("Error while calling Keycloak token endpoint", e);
        }
        if(response == null || response.accessToken() == null || response.expiresIn() == null) {
            throw new KeycloakException("Failed to obtain access token from Keycloak");
        }
        this.cachedToken = response.accessToken();
        this.expiresAt = Instant.now()
                .plusSeconds(response.expiresIn() - CLOCK_SKEW_SECONDS);
        return cachedToken;
    }
}
