package io.github.gabrielvelosoo.authservice.infrastructure.otp;

import java.security.SecureRandom;

public class OtpUtil {

    private static final SecureRandom rand = new SecureRandom();

    public static String generate6Digits() {
        int n = rand.nextInt(900_000) + 100_000;
        return Integer.toString(n);
    }
}
