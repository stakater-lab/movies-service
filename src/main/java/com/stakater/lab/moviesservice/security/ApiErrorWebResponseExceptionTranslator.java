package com.stakater.lab.moviesservice.security;

import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.provider.error.DefaultWebResponseExceptionTranslator;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;

public class ApiErrorWebResponseExceptionTranslator implements WebResponseExceptionTranslator<ApiError> {

    /** The default WebResponseExceptionTranslator. */
    private final WebResponseExceptionTranslator<OAuth2Exception> defaultTranslator;

    public ApiErrorWebResponseExceptionTranslator() {
        this.defaultTranslator = new DefaultWebResponseExceptionTranslator();
    }

    @Override
    public ResponseEntity<ApiError> translate(Exception e) throws Exception {
        // Translate the exception with the default translator
        ResponseEntity<OAuth2Exception> defaultResponse = defaultTranslator.translate(e);
        // Build your own error object
        String errorCode = defaultResponse.getBody().getOAuth2ErrorCode();
        ApiError apiError = ApiError.newBuilder().status(defaultResponse.getStatusCode().toString()).title(errorCode).detail(defaultResponse.getBody().getMessage()).build();
        // Use the same status code as the default OAuth2 error
        return new ResponseEntity<>(apiError, defaultResponse.getStatusCode());
    }
}
