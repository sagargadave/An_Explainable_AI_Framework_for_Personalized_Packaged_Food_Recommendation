package com.packagedfood.recommendation.exception;

import java.util.List;

public class InsufficientNutritionDataException extends RuntimeException {

    private final List<String> missingFields;

    public InsufficientNutritionDataException(
            List<String> missingFields) {

        super("Required nutrition data is missing.");

        this.missingFields = missingFields;
    }

    public List<String> getMissingFields() {
        return missingFields;
    }
}