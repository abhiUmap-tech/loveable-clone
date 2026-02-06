package com.projects.lovable_clone.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.List;

public record APIError(
        HttpStatus httpStatus,
        String message,
        Instant timeStamp,
        @JsonInclude(JsonInclude.Include.NON_NULL) List<ApiFieldError> errors) {

    public APIError(HttpStatus httpStatus, String message){
        this(httpStatus, message, Instant.now(), null);
    }

    public APIError(HttpStatus httpStatus, String message, List<ApiFieldError> errors){
        this(httpStatus, message, Instant.now(), errors);
    }

}

record ApiFieldError(String field, String message){}
