package com.country.app.controllers;


import com.country.app.dto.ApiKeyResponse;
import com.country.app.models.ApiKey;
import com.country.app.models.User;
import com.country.app.service.ApiKeyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/api/keys")
@RequiredArgsConstructor
public class ApiKeyController {

    private final ApiKeyService apiKeyService;

    // Web interface endpoints

    @GetMapping
    public String showApiKeys(@AuthenticationPrincipal User user, Model model) {
        List<ApiKeyResponse> apiKeys = apiKeyService.getUserApiKeys(user);
        model.addAttribute("apiKeys", apiKeys);
        return "api-keys";
    }

    @PostMapping("/generate")
    public String generateApiKey(@AuthenticationPrincipal User user,
                                 @RequestParam String keyName) {
        apiKeyService.generateApiKey(user, keyName);
        return "redirect:/api/keys";
    }

    @PostMapping("/revoke/{keyId}")
    public String revokeApiKey(@AuthenticationPrincipal User user,
                               @PathVariable Long keyId) {
        apiKeyService.revokeApiKey(keyId, user);
        return "redirect:/api/keys";
    }

    // REST API endpoints for programmatic access

    @GetMapping("/api")
    @ResponseBody
    public ResponseEntity<List<ApiKeyResponse>> getUserApiKeys(@AuthenticationPrincipal User user) {
        List<ApiKeyResponse> apiKeys = apiKeyService.getUserApiKeys(user);
        return ResponseEntity.ok(apiKeys);
    }

    @PostMapping("/api/generate")
    @ResponseBody
    public ResponseEntity<ApiKeyResponse> generateApiKeyRest(@AuthenticationPrincipal User user,
                                                             @RequestBody Map<String, String> request) {
        ApiKey apiKey = apiKeyService.generateApiKey(user, request.get("name"));
        ApiKeyResponse response = ApiKeyResponse.builder()
                .id(apiKey.getId())
                .key(apiKey.getKey())
                .name(apiKey.getName())
                .createdAt(apiKey.getCreatedAt().toString())
                .expiresAt(apiKey.getExpiresAt().toString())
                .active(apiKey.isActive())
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/api/revoke/{keyId}")
    @ResponseBody
    public ResponseEntity<?> revokeApiKeyRest(@AuthenticationPrincipal User user,
                                              @PathVariable Long keyId) {
        apiKeyService.revokeApiKey(keyId, user);
        return ResponseEntity.ok().body(Map.of("message", "API key revoked successfully"));
    }
}