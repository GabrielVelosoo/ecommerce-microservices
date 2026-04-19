package io.github.gabrielvelosoo.authservice.infrastructure.crypto;

import io.github.gabrielvelosoo.authservice.domain.service.CryptoService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

@Service
public class AesCryptoService implements CryptoService {

    private static final int GCM_TAG_LENGTH = 128;

    private final SecretKeySpec keySpec;
    private final SecureRandom random = new SecureRandom();

    public AesCryptoService(@Value("${app.crypto.key}") String base64Key) {
        byte[] key = Base64.getDecoder().decode(base64Key);
        this.keySpec = new SecretKeySpec(key, "AES");
    }

    @Override
    public String encrypt(String plaintext) throws Exception {
        byte[] iv = new byte[12];
        random.nextBytes(iv);
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, spec);
        byte[] cipherText = cipher.doFinal(plaintext.getBytes());
        byte[] combined = new byte[iv.length + cipherText.length];
        System.arraycopy(iv, 0, combined, 0, iv.length);
        System.arraycopy(cipherText, 0, combined, iv.length, cipherText.length);
        return Base64.getEncoder().encodeToString(combined);
    }

    @Override
    public String decrypt(String base64) throws Exception {
        byte[] combined = Base64.getDecoder().decode(base64);
        byte[] iv = new byte[12];
        System.arraycopy(combined, 0, iv, 0, iv.length);
        byte[] cipherText = new byte[combined.length - iv.length];
        System.arraycopy(combined, iv.length, cipherText, 0, cipherText.length);
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        cipher.init(Cipher.DECRYPT_MODE, keySpec, spec);
        byte[] plain = cipher.doFinal(cipherText);
        return new String(plain);
    }
}
