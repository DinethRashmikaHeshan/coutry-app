package com.country.app.filter;


import com.country.app.exception.ApiKeyException;
import com.country.app.models.ApiKey;
import com.country.app.service.ApiKeyService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class ApiKeyAuthFilter extends OncePerRequestFilter {

    private final ApiKeyService apiKeyService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String apiKey = extractApiKey(request);

        if (apiKey == null) {
            handleUnauthorized(response, "API key is missing");
            return;
        }

        try {
            // Validate the API key
            ApiKey validatedKey = apiKeyService.validateApiKey(apiKey);

            // Set authentication in SecurityContext
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    validatedKey.getUser(), null, validatedKey.getUser().getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Log the API key usage (async to not block the request)
            logApiKeyUsage(validatedKey, request, response);

            // Continue with the filter chain
            filterChain.doFilter(request, response);
        } catch (ApiKeyException e) {
            handleUnauthorized(response, e.getMessage());
        }
    }

    private String extractApiKey(HttpServletRequest request) {
        // First check header
        String apiKey = request.getHeader("X-API-KEY");

        // If not in header, check query parameter
        if (apiKey == null || apiKey.isEmpty()) {
            apiKey = request.getParameter("api_key");
        }

        return apiKey;
    }

    private void handleUnauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("status", HttpStatus.UNAUTHORIZED.value());
        errorDetails.put("error", "Unauthorized");
        errorDetails.put("message", message);

        response.getWriter().write(objectMapper.writeValueAsString(errorDetails));
    }

    private void logApiKeyUsage(ApiKey apiKey, HttpServletRequest request, HttpServletResponse response) {
        String endpoint = request.getRequestURI();
        String requestIp = request.getRemoteAddr();

        // We'll log this after the request is complete to capture the response status
        // This is done asynchronously to not block the response
        request.setAttribute("apiKey", apiKey);
        request.setAttribute("endpoint", endpoint);
        request.setAttribute("requestIp", requestIp);
    }
}