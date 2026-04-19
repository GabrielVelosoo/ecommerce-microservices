package io.github.gabrielvelosoo.authservice.domain.service;

public interface EmailService {

    void sendRegisterOtpEmail(String to, String code);
}
