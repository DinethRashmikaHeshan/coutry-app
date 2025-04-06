package com.country.app.util;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Base64;

@Component
public class ApiKeyGenerator {

    private static final SecureRandom secureRandom = new SecureRandom();

    /**
     * Generates a secure API key of the specified length
     *
     * @param length The desired length for the API key
     * @return A secure random API key
     */
    public String generateKey(int length) {
        byte[] randomBytes = new byte[length];
        secureRandom.nextBytes(randomBytes);

        // Convert to Base64 and remove non-alphanumeric characters
        String base64Key = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);

        // Ensure the length is as requested (Base64 encoding can change length)
        return base64Key.replaceAll("[^a-zA-Z0-9]", "").substring(0, Math.min(base64Key.length(), length));
    }
}