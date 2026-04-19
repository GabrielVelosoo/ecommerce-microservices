package io.github.gabrielvelosoo.authservice.application.usecase;

import io.github.gabrielvelosoo.authservice.application.dto.register.RegisterOtpPayload;
import io.github.gabrielvelosoo.authservice.application.dto.register.ResendRegisterCode;
import io.github.gabrielvelosoo.authservice.application.exception.NotFoundException;
import io.github.gabrielvelosoo.authservice.domain.service.EmailService;
import io.github.gabrielvelosoo.authservice.domain.service.OtpService;
import io.github.gabrielvelosoo.authservice.infrastructure.otp.OtpUtil;
import org.springframework.stereotype.Component;

@Component
public class RegisterResendCodeUseCase {

    private final OtpService otpService;
    private final EmailService emailService;

    public RegisterResendCodeUseCase(OtpService otpService, EmailService emailService) {
        this.otpService = otpService;
        this.emailService = emailService;
    }

    public void execute(ResendRegisterCode request) {
        String emailNormalized = request.email().trim().toLowerCase();
        RegisterOtpPayload payload = otpService
                .getExistingRegisterPayload(emailNormalized)
                .orElseThrow(() ->
                        new NotFoundException("Registration not found or expired")
                );
        String newCode = OtpUtil.generate6Digits();
        otpService.saveRegisterOtp(emailNormalized, newCode, payload);
        emailService.sendRegisterOtpEmail(emailNormalized, newCode);
    }
}
