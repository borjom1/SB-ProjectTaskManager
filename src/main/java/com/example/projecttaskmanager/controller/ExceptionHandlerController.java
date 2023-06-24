package com.example.projecttaskmanager.controller;

import com.example.projecttaskmanager.dto.ApiError;
import com.example.projecttaskmanager.exception.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

import static java.time.ZonedDateTime.now;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;

@Slf4j
@RestControllerAdvice
public class ExceptionHandlerController {

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, Object> handleValidationExceptions(MethodArgumentNotValidException e, HttpServletRequest request) {
        log.warn("handleValidationExceptions(): {}", e.getMessage());
        Map<String, Object> errors = new LinkedHashMap<>();

        errors.put("path", request.getRequestURI());
        e.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
        errors.put("status", BAD_REQUEST.value());
        errors.put("timestamp", now());

        return errors;
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler({
            LoginAlreadyExistsException.class,
            CredentialsNotMatchException.class,
            UserNotFoundException.class,
            StoryNotFoundException.class
    })
    public ApiError handle(Exception e, HttpServletRequest request) {
        log.warn("handle(): {}", e.getMessage());
        return new ApiError(request.getRequestURI(), e.getMessage(), BAD_REQUEST.value(), now());
    }

    @ResponseStatus(FORBIDDEN)
    @ExceptionHandler(FakeMemberException.class)
    public ApiError handleForbidden(Exception e, HttpServletRequest request) {
        log.warn("handleForbidden(): {}", e.getMessage());
        return new ApiError(request.getRequestURI(), e.getMessage(), FORBIDDEN.value(), now());
    }

}