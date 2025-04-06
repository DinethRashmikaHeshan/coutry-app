package com.country.app.service;

import com.country.app.dto.ApiKeyResponse;
import com.country.app.exception.ApiKeyException;
import com.country.app.models.ApiKey;
import com.country.app.models.ApiKeyUsage;
import com.country.app.models.User;
import com.country.app.repository.ApiKeyRepository;
import com.country.app.repository.ApiKeyUsageRepository;
import com.country.app.util.ApiKeyGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApiKeyService {

    private final ApiKeyRepository apiKeyRepository;
    private final ApiKeyUsageRepository apiKeyUsageRepository;
    private final ApiKeyGenerator apiKeyGenerator;

    @Value("${api.key.length:32}")
    private int apiKeyLength;

    @Value("${api.key.prefix:API_}")
    private String apiKeyPrefix;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Transactional
    public ApiKey generateApiKey(User user, String keyName) {
        // Generate a unique key
        String key;
        do {
            key = apiKeyPrefix + apiKeyGenerator.generateKey(apiKeyLength);
        } while (apiKeyRepository.existsByKey(key));

        // Create and save the API key
        ApiKey apiKey = ApiKey.builder()
                .user(user)
                .key(key)
                .name(keyName)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusYears(1)) // Default expiry 1 year
                .active(true)
                .build();

        return apiKeyRepository.save(apiKey);
    }

    @Transactional(readOnly = true)
    public List<ApiKeyResponse> getUserApiKeys(User user) {
        List<ApiKey> apiKeys = apiKeyRepository.findByUser(user);

        return apiKeys.stream()
                .map(this::mapToApiKeyResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void revokeApiKey(Long apiKeyId, User user) {
        ApiKey apiKey = apiKeyRepository.findById(apiKeyId)
                .orElseThrow(() -> new ApiKeyException("API key not found"));

        // Ensure the key belongs to the user
        if (!apiKey.getUser().getId().equals(user.getId())) {
            throw new ApiKeyException("You do not have permission to revoke this API key");
        }

        // Revoke the key
        apiKey.setActive(false);
        apiKeyRepository.save(apiKey);
    }

    @Transactional(readOnly = true)
    public ApiKey validateApiKey(String key) {
        ApiKey apiKey = apiKeyRepository.findByKey(key)
                .orElseThrow(() -> new ApiKeyException("Invalid API key"));

        if (!apiKey.isActive()) {
            throw new ApiKeyException("API key is inactive");
        }

        if (apiKey.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ApiKeyException("API key has expired");
        }

        return apiKey;
    }

    @Transactional
    public void logApiKeyUsage(ApiKey apiKey, String endpoint, String requestIp, Integer responseStatus) {
        ApiKeyUsage usage = ApiKeyUsage.builder()
                .apiKey(apiKey)
                .endpoint(endpoint)
                .requestIp(requestIp)
                .requestedAt(LocalDateTime.now())
                .responseStatus(responseStatus)
                .build();

        apiKeyUsageRepository.save(usage);
    }

    private ApiKeyResponse mapToApiKeyResponse(ApiKey apiKey) {
        return ApiKeyResponse.builder()
                .id(apiKey.getId())
                .key(apiKey.getKey())
                .name(apiKey.getName())
                .createdAt(apiKey.getCreatedAt().format(DATE_FORMATTER))
                .expiresAt(apiKey.getExpiresAt().format(DATE_FORMATTER))
                .active(apiKey.isActive())
                .build();
    }
}