package com.country.app.service;


import com.country.app.dto.CountryDTO;
import com.country.app.exception.CountryApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CountryService {

    private final WebClient.Builder webClientBuilder;

    @Value("${restcountries.api.url}")
    private String restCountriesApiUrl;

    public Flux<CountryDTO> getAllCountries() {
        return webClientBuilder.build()
                .get()
                .uri(restCountriesApiUrl + "/all")
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), response ->
                        response.bodyToMono(String.class)
                                .flatMap(error -> Mono.error(new CountryApiException("Error fetching all countries: " + error)))
                )
                .bodyToFlux(Map.class)
                .map(this::mapToCountryDTO);
    }

    public Mono<CountryDTO> getCountryByName(String name) {
        return webClientBuilder.build()
                .get()
                .uri(restCountriesApiUrl + "/name/" + name)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), response ->
                        response.bodyToMono(String.class)
                                .flatMap(error -> Mono.error(new CountryApiException("Error fetching country by name: " + error)))
                )
                .bodyToFlux(Map.class)
                .map(this::mapToCountryDTO)
                .next();
    }

    public Flux<CountryDTO> getCountriesByRegion(String region) {
        return webClientBuilder.build()
                .get()
                .uri(restCountriesApiUrl + "/region/" + region)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), response ->
                        response.bodyToMono(String.class)
                                .flatMap(error -> Mono.error(new CountryApiException("Error fetching countries by region: " + error)))
                )
                .bodyToFlux(Map.class)
                .map(this::mapToCountryDTO);
    }

    @SuppressWarnings("unchecked")
    private CountryDTO mapToCountryDTO(Map<String, Object> countryData) {
        CountryDTO dto = new CountryDTO();

        // Extract country name
        if (countryData.containsKey("name")) {
            Map<String, Object> nameData = (Map<String, Object>) countryData.get("name");
            dto.setName((String) nameData.get("common"));
        }

        // Extract currencies
        if (countryData.containsKey("currencies")) {
            Map<String, Object> currenciesData = (Map<String, Object>) countryData.get("currencies");
            Map<String, CountryDTO.CurrencyInfo> currencies = new HashMap<>();

            for (Map.Entry<String, Object> entry : currenciesData.entrySet()) {
                Map<String, Object> currencyDetails = (Map<String, Object>) entry.getValue();
                CountryDTO.CurrencyInfo currencyInfo = new CountryDTO.CurrencyInfo(
                        (String) currencyDetails.get("name"),
                        (String) currencyDetails.get("symbol")
                );
                currencies.put(entry.getKey(), currencyInfo);
            }
            dto.setCurrencies(currencies);
        }

        // Extract capital city
        if (countryData.containsKey("capital") && ((List<String>) countryData.get("capital")).size() > 0) {
            List<String> capitals = (List<String>) countryData.get("capital");
            dto.setCapital(capitals.get(0));
        }

        // Extract languages
        if (countryData.containsKey("languages")) {
            dto.setLanguages((Map<String, String>) countryData.get("languages"));
        }

        // Extract flag
        if (countryData.containsKey("flags")) {
            Map<String, Object> flagsData = (Map<String, Object>) countryData.get("flags");
            dto.setFlag((String) flagsData.get("png"));
        }

        return dto;
    }
}