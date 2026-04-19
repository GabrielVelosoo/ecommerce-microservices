package io.github.gabrielvelosoo.authservice.domain.service;

public interface CryptoService {

    String encrypt(String plaintext) throws Exception;
    String decrypt(String base64) throws Exception;
}
