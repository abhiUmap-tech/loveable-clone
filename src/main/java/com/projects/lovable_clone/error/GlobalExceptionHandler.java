package com.projects.lovable_clone.error;


import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<APIError> handleBadRequestException(BadRequestException badRequestException){
        var apiError = new APIError(HttpStatus.BAD_REQUEST, badRequestException.getMessage());
        log.error(apiError.toString(), badRequestException);
        return ResponseEntity.status(apiError.httpStatus()).body(apiError);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<APIError> handleResourceNotFoundException(ResourceNotFoundException resourceNotFoundException){
        var apiError = new APIError(HttpStatus.NOT_FOUND, resourceNotFoundException.getResourceName() + " with the id " + resourceNotFoundException.getResourceId() + " not found");
        log.error(apiError.toString(), resourceNotFoundException);
        return ResponseEntity.status(apiError.httpStatus()).body(apiError);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<APIError> handleMethodArgumentNotValidException(MethodArgumentNotValidException methodArgumentNotValidException){

       var apiFieldErrors = methodArgumentNotValidException.getBindingResult()
               .getFieldErrors()
               .stream()
               .map(error -> new ApiFieldError(error.getField(), error.getDefaultMessage()))
               .toList();

       var apiError = new APIError(HttpStatus.BAD_REQUEST,
               "Input Validation Failed",
               apiFieldErrors);

       log.error(apiError.toString(), methodArgumentNotValidException);

       return ResponseEntity.status(apiError.httpStatus()).body(apiError);

    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<APIError> handleUsernameNotFoundException(UsernameNotFoundException usernameNotFoundException){
        var apiError = new APIError(HttpStatus.NOT_FOUND, "Username not found with username::" + usernameNotFoundException.getMessage());
        log.error(apiError.toString(), usernameNotFoundException);
        return ResponseEntity.status(apiError.httpStatus()).body(apiError);
    }





}
