package com.stakater.lab.moviesservice.security;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Value;

import java.io.Serializable;

@Value
@JsonPropertyOrder({
        ApiFieldError.OBJECT_NAME,
        ApiFieldError.FIELD,
        ApiFieldError.MESSAGE
})
public class ApiFieldError implements Serializable {

    public static final String OBJECT_NAME = "object_name";
    public static final String FIELD = "field";
    public static final String MESSAGE = "message";

    @JsonProperty(value = ApiFieldError.OBJECT_NAME)
    private final String objectName;

    private final String field;

    private final String message;

    @lombok.Builder(builderClassName = "Builder", builderMethodName = "newBuilder", toBuilder = true)
    private ApiFieldError(String objectName, String field, String message) {
        this.objectName = objectName;
        this.field = field;
        this.message = message;
    }
}
