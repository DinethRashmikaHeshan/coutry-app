package com.country.app.controllers;


import com.country.app.dto.CountryDTO;
import com.country.app.models.ApiKey;
import com.country.app.service.ApiKeyService;
import com.country.app.service.CountryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/countries")
@RequiredArgsConstructor
public class CountryController {

    private final CountryService countryService;
    private final ApiKeyService apiKeyService;

    @GetMapping
    public ResponseEntity<Flux<CountryDTO>> getAllCountries(HttpServletRequest request) {
        logApiKeyUsage(request);
        return ResponseEntity.ok(countryService.getAllCountries());
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<Mono<CountryDTO>> getCountryByName(@PathVariable String name, HttpServletRequest request) {
        logApiKeyUsage(request);
        return ResponseEntity.ok(countryService.getCountryByName(name));
    }

    @GetMapping("/region/{region}")
    public ResponseEntity<Flux<CountryDTO>> getCountriesByRegion(@PathVariable String region, HttpServletRequest request) {
        logApiKeyUsage(request);
        return ResponseEntity.ok(countryService.getCountriesByRegion(region));
    }

    private void logApiKeyUsage(HttpServletRequest request) {
        // The API key and other attributes were set in the ApiKeyAuthFilter
        ApiKey apiKey = (ApiKey) request.getAttribute("apiKey");
        String endpoint = (String) request.getAttribute("endpoint");
        String requestIp = (String) request.getAttribute("requestIp");

        if (apiKey != null) {
            apiKeyService.logApiKeyUsage(apiKey, endpoint, requestIp, 200);
        }
    }
}