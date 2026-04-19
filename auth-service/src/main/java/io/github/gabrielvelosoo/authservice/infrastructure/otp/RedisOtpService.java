package io.github.gabrielvelosoo.authservice.infrastructure.otp;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.gabrielvelosoo.authservice.application.dto.register.RegisterOtpPayload;
import io.github.gabrielvelosoo.authservice.domain.service.CryptoService;
import io.github.gabrielvelosoo.authservice.domain.service.OtpService;
import io.github.gabrielvelosoo.authservice.application.exception.OtpAttemptsExceededException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.util.Optional;

@Service
public class RedisOtpService implements OtpService {

    private final StringRedisTemplate redis;
    private final ObjectMapper mapper;
    private final CryptoService crypto;
    private final Duration ttl;
    private final long maxSendsPerHour;

    public RedisOtpService(
            StringRedisTemplate redis,
            ObjectMapper mapper,
            CryptoService crypto,
            OtpProperties properties
    ) {
        this.redis = redis;
        this.mapper = mapper;
        this.crypto = crypto;
        this.ttl = Duration.ofMinutes(properties.ttlMinutes());
        this.maxSendsPerHour = properties.maxSendsPerHour();
    }

    @Override
    public void saveRegisterOtp(String email, String code, RegisterOtpPayload payload) {
        String key = keyForEmail(email);
        try {
            String payloadJson = mapper.writeValueAsString(payload);
            String encrypted = crypto.encrypt(payloadJson);
            String value = code + "|" + encrypted;
            redis.opsForValue().set(key, value, ttl);
            String sends = sendsKey(email);
            Long sendsCount = incrementSendCounter(sends);
            validateRateLimit(sendsCount);
        } catch (OtpAttemptsExceededException e) {
            throw e;
        } catch(Exception e) {
            throw new RuntimeException("Failed to save OTP", e);
        }
    }

    private Long incrementSendCounter(String key) {
        Long count = redis.opsForValue().increment(key);
        if(count == null) {
            throw new IllegalStateException("Failed to increment OTP send counter");
        }
        if(count == 1) {
            redis.expire(key, Duration.ofHours(1));
        }
        return count;
    }

    private void validateRateLimit(Long count) {
        if(count > maxSendsPerHour) {
            throw new OtpAttemptsExceededException("Too many attempts to send code");
        }
    }

    @Override
    public Optional<RegisterOtpPayload> validateAndConsumeRegisterOtp(String email, String code) {
        String key = keyForEmail(email);
        String value = redis.opsForValue().get(key);
        if(value == null || value.isBlank()) {
            return Optional.empty();
        }
        String[] parts = value.split("\\|", 2);
        if(parts.length < 2) {
            return Optional.empty();
        }
        String storedCode = parts[0];
        String encryptedPayload = parts[1];
        if(!MessageDigest.isEqual(
                code.getBytes(StandardCharsets.UTF_8),
                storedCode.getBytes(StandardCharsets.UTF_8)
        )) {
            return Optional.empty();
        }
        try {
            String payloadJson = crypto.decrypt(encryptedPayload);
            RegisterOtpPayload payload = mapper.readValue(payloadJson, RegisterOtpPayload.class);
            redis.delete(key);
            return Optional.of(payload);
        } catch(Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<RegisterOtpPayload> getExistingRegisterPayload(String email) {
        String key = keyForEmail(email);
        String value = redis.opsForValue().get(key);
        if(value == null || value.isBlank()) {
            return Optional.empty();
        }
        String[] parts = value.split("\\|", 2);
        if(parts.length < 2) {
            return Optional.empty();
        }
        String encryptedPayload = parts[1];
        try {
            String payloadJson = crypto.decrypt(encryptedPayload);
            RegisterOtpPayload payload = mapper.readValue(payloadJson, RegisterOtpPayload.class);
            return Optional.of(payload);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private String keyForEmail(String email) {
        return "otp:register:email:" + email;
    }

    private String sendsKey(String email) {
        return "otp:register:email:sends:" + email;
    }
}
