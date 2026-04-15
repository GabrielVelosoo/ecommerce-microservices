package io.github.gabrielvelosoo.apigateway.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.ReactiveHealthIndicator;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class KeycloakHealthIndicator implements ReactiveHealthIndicator {

    private final WebClient webClient;

    public KeycloakHealthIndicator(WebClient.Builder builder) {
        this.webClient = builder.baseUrl("http://localhost:8080").build();
    }

    @Override
    public Mono<Health> health() {
        return webClient.get()
                .uri("/realms/ecommerce")
                .retrieve()
                .toBodilessEntity()
                .map(res -> Health.up().build())
                .onErrorResume(ex ->
                        Mono.just(Health.down(ex).build())
                );
    }
}
