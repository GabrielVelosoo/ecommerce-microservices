package io.github.gabrielvelosoo.authservice.infrastructure.email;

import io.github.gabrielvelosoo.authservice.application.exception.TechnicalException;
import io.github.gabrielvelosoo.authservice.domain.service.EmailService;
import io.github.gabrielvelosoo.authservice.infrastructure.email.config.BrevoProperties;
import io.github.gabrielvelosoo.authservice.infrastructure.otp.OtpProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
public class BrevoEmailService implements EmailService {

    private final WebClient webClient;
    private final String fromAddress;
    private final long codeTtlMinutes;

    public BrevoEmailService(
            WebClient brevoWebClient,
            BrevoProperties brevoProperties,
            OtpProperties otpProperties
    ) {
        this.webClient = brevoWebClient;
        this.fromAddress = brevoProperties.from();
        this.codeTtlMinutes = otpProperties.ttlMinutes();
    }

    @Override
    public void sendRegisterOtpEmail(String to, String code) {
        try {
            Map<String, Object> payload = Map.of(
                    "sender", Map.of("email", fromAddress),
                    "to", new Object[]{ Map.of("email", to) },
                    "subject", "Verify your e-mail",
                    "htmlContent", buildRegisterOtpBody(code)
            );
            webClient.post()
                    .uri("/smtp/email")
                    .bodyValue(payload)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } catch(Exception e) {
            throw new TechnicalException("Failed to send verification e-mail via Brevo", e);
        }
    }

    private String buildRegisterOtpBody(String code) {
        return """
                <html>
                  <body style="font-family: Arial, sans-serif;">
                    <h2>Email Verification</h2>
                    <p>Your verification code is:</p>
                    <h1 style="letter-spacing: 4px;">%s</h1>
                    <p>This code expires in %d minutes.</p>
                    <p>If you did not request this, ignore this email.</p>
                  </body>
                </html>
                """.formatted(code, codeTtlMinutes);
    }
}
