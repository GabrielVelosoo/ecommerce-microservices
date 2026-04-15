package io.github.gabrielvelosoo.authservice.application.usecase;

import io.github.gabrielvelosoo.authservice.application.dto.email.CheckEmailRequest;
import io.github.gabrielvelosoo.authservice.application.dto.email.CheckEmailResponse;
import io.github.gabrielvelosoo.authservice.domain.repository.IdentityProviderRepository;
import io.github.gabrielvelosoo.authservice.domain.auth.AuthFlow;
import org.springframework.stereotype.Component;

@Component
public class CheckEmailUseCase {

    private final IdentityProviderRepository identityProviderRepository;

    public CheckEmailUseCase(IdentityProviderRepository identityProviderRepository) {
        this.identityProviderRepository = identityProviderRepository;
    }

    public CheckEmailResponse execute(CheckEmailRequest request) {
        boolean exists = identityProviderRepository.existsByEmail(request.email());
        AuthFlow flow = exists ? AuthFlow.LOGIN : AuthFlow.REGISTER;
        return new CheckEmailResponse(exists, flow);
    }
}
