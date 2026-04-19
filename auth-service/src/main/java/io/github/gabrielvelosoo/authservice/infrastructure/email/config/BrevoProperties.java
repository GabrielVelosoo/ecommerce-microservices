package io.github.gabrielvelosoo.authservice.infrastructure.email.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.mail.brevo")
public record BrevoProperties(
        String from,
        String apiKey
    ) {
}
