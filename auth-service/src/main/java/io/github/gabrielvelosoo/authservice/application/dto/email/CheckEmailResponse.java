package io.github.gabrielvelosoo.authservice.application.dto.email;

import io.github.gabrielvelosoo.authservice.domain.auth.AuthFlow;

public record CheckEmailResponse(
        boolean exists,
        AuthFlow flow
    ) {
}
