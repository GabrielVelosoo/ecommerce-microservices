package io.github.gabrielvelosoo.authservice.application.usecase;

import io.github.gabrielvelosoo.authservice.application.dto.register.RegisterOtpPayload;
import io.github.gabrielvelosoo.authservice.application.dto.register.RegisterUserCommand;
import io.github.gabrielvelosoo.authservice.application.dto.register.VerifyRegisterCode;
import io.github.gabrielvelosoo.authservice.application.exception.ConflictException;
import io.github.gabrielvelosoo.authservice.application.exception.InvalidOrExpiredOtpException;
import io.github.gabrielvelosoo.authservice.domain.repository.IdentityProviderRepository;
import io.github.gabrielvelosoo.authservice.domain.service.OtpService;
import org.springframework.stereotype.Component;

@Component
public class RegisterVerifyCodeUseCase {

    private final IdentityProviderRepository identityProvider;
    private final OtpService otpService;

    public RegisterVerifyCodeUseCase(IdentityProviderRepository identityProvider, OtpService otpService) {
        this.identityProvider = identityProvider;
        this.otpService = otpService;
    }

    public void execute(VerifyRegisterCode request) {
        String email = request.email().trim().toLowerCase();
        if(identityProvider.existsByEmail(email)) {
            throw new ConflictException("E-mail already registered");
        }
        RegisterOtpPayload payload = otpService
                .validateAndConsumeRegisterOtp(email, request.code())
                .orElseThrow(() ->
                        new InvalidOrExpiredOtpException("Invalid or expired verification code"));
        identityProvider.createUser(
                new RegisterUserCommand(
                        email,
                        payload.firstName(),
                        payload.lastName(),
                        payload.password()
                )
        );
    }
}
