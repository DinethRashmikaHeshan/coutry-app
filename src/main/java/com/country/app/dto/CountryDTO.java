package com.country.app.dto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CountryDTO {
    private String name;
    private Map<String, CurrencyInfo> currencies;
    private String capital;
    private Map<String, String> languages;
    private String flag;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CurrencyInfo {
        private String name;
        private String symbol;
    }
}