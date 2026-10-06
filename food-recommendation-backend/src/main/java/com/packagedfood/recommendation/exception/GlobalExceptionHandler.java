package com.packagedfood.recommendation.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InsufficientNutritionDataException.class)
    public ResponseEntity<Map<String, Object>>
    handleInsufficientNutritionData(
            InsufficientNutritionDataException exception) {

        Map<String, Object> response = new HashMap<>();

        response.put("timestamp", Instant.now());
        response.put("status", 400);
        response.put("error", "INSUFFICIENT_NUTRITION_DATA");
        response.put(
                "message",
                "Nutrition analysis cannot be performed because " +
                        "one or more required nutrition values are missing."
        );
        response.put(
                "missingFields",
                exception.getMissingFields()
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(AiServiceException.class)
    public ResponseEntity<Map<String, Object>>
    handleAiServiceException(
            AiServiceException exception) {

        Map<String, Object> response = new HashMap<>();

        response.put("timestamp", Instant.now());
        response.put("status", 503);
        response.put("error", "AI_SERVICE_UNAVAILABLE");
        response.put("message", exception.getMessage());

        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(response);
    }
}