package io.github.gabrielvelosoo.authservice.application.usecase;

import io.github.gabrielvelosoo.authservice.application.dto.register.RegisterOtpPayload;
import io.github.gabrielvelosoo.authservice.application.dto.register.SendRegisterCode;
import io.github.gabrielvelosoo.authservice.application.exception.ConflictException;
import io.github.gabrielvelosoo.authservice.domain.repository.IdentityProviderRepository;
import io.github.gabrielvelosoo.authservice.domain.service.EmailService;
import io.github.gabrielvelosoo.authservice.domain.service.OtpService;
import io.github.gabrielvelosoo.authservice.infrastructure.otp.OtpUtil;
import org.springframework.stereotype.Component;

@Component
public class RegisterSendCodeUseCase {

    private final IdentityProviderRepository identityProvider;
    private final OtpService otpService;
    private final EmailService emailService;

    public RegisterSendCodeUseCase(IdentityProviderRepository identityProvider, OtpService otpService, EmailService emailService) {
        this.identityProvider = identityProvider;
        this.otpService = otpService;
        this.emailService = emailService;
    }

    public void execute(SendRegisterCode request) {
        String emailNormalized = request.email().trim().toLowerCase();
        if(identityProvider.existsByEmail(emailNormalized)) {
            throw new ConflictException("E-mail already registered");
        }
        String code = OtpUtil.generate6Digits();
        RegisterOtpPayload payload = new RegisterOtpPayload(
                emailNormalized,
                request.firstName(),
                request.lastName(),
                request.password()
        );
        otpService.saveRegisterOtp(emailNormalized, code, payload);
        emailService.sendRegisterOtpEmail(emailNormalized, code);
    }
}
