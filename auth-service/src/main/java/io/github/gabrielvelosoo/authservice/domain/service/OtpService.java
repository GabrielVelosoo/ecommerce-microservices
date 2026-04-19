package io.github.gabrielvelosoo.authservice.domain.service;

import io.github.gabrielvelosoo.authservice.application.dto.register.RegisterOtpPayload;

import java.util.Optional;

public interface OtpService {

    void saveRegisterOtp(String email, String code, RegisterOtpPayload payload);
    Optional<RegisterOtpPayload> validateAndConsumeRegisterOtp(String email, String code);
    Optional<RegisterOtpPayload> getExistingRegisterPayload(String email);
}
