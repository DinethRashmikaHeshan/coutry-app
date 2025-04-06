package com.country.app.exception;

public class CountryApiException extends RuntimeException {

    public CountryApiException(String message) {
        super(message);
    }

    public CountryApiException(String message, Throwable cause) {
        super(message, cause);
    }
}