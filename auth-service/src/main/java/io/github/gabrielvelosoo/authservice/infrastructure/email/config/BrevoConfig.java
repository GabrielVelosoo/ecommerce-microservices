package io.github.gabrielvelosoo.authservice.infrastructure.email.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class BrevoConfig {

    @Bean
    public WebClient brevoWebClient(BrevoProperties properties) {
        return WebClient.builder()
                .baseUrl("https://api.brevo.com/v3")
                .defaultHeader("api-key", properties.apiKey())
                .defaultHeader("Content-Type", "application/json")
                .build();
    }
}
