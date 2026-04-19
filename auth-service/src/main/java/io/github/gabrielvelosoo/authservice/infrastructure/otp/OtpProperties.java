package io.github.gabrielvelosoo.authservice.infrastructure.otp;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.otp")
public record OtpProperties(
        long ttlMinutes,
        long maxSendsPerHour
    ) {
}
