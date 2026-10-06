package com.packagedfood.recommendation.service;

import com.packagedfood.recommendation.client.AiPredictionClient;
import com.packagedfood.recommendation.dto.AiPredictionRequest;
import com.packagedfood.recommendation.dto.AiPredictionResponse;
import com.packagedfood.recommendation.dto.ProductAnalysisRequest;
import com.packagedfood.recommendation.exception.InsufficientNutritionDataException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductAnalysisService {

    private final AiPredictionClient aiPredictionClient;

    public ProductAnalysisService(
            AiPredictionClient aiPredictionClient) {

        this.aiPredictionClient = aiPredictionClient;
    }

    public AiPredictionResponse analyzeProduct(
            ProductAnalysisRequest product) {

        validateNutritionData(product);

        AiPredictionRequest request =
                new AiPredictionRequest();

        request.setEnergy(product.getEnergy100g());
        request.setFat(product.getFat100g());
        request.setSaturatedFat(product.getSaturatedFat100g());
        request.setCarbohydrates(
                product.getCarbohydrates100g()
        );
        request.setSugars(product.getSugars100g());
        request.setFiber(product.getFiber100g());
        request.setProteins(product.getProteins100g());
        request.setSalt(product.getSalt100g());

        return aiPredictionClient.predict(request);
    }

    private void validateNutritionData(
            ProductAnalysisRequest product) {

        List<String> missingFields =
                new ArrayList<>();

        if (product == null) {
            missingFields.add("nutrition data");
            throw new InsufficientNutritionDataException(
                    missingFields
            );
        }

        if (isInvalid(product.getEnergy100g())) {
            missingFields.add("energy100g");
        }

        if (isInvalid(product.getFat100g())) {
            missingFields.add("fat100g");
        }

        if (isInvalid(product.getSaturatedFat100g())) {
            missingFields.add("saturatedFat100g");
        }

        if (isInvalid(product.getCarbohydrates100g())) {
            missingFields.add("carbohydrates100g");
        }

        if (isInvalid(product.getSugars100g())) {
            missingFields.add("sugars100g");
        }

        if (isInvalid(product.getFiber100g())) {
            missingFields.add("fiber100g");
        }

        if (isInvalid(product.getProteins100g())) {
            missingFields.add("proteins100g");
        }

        if (isInvalid(product.getSalt100g())) {
            missingFields.add("salt100g");
        }

        if (!missingFields.isEmpty()) {
            throw new InsufficientNutritionDataException(
                    missingFields
            );
        }
    }

    private boolean isInvalid(Double value) {

        return value == null
                || value.isNaN()
                || value.isInfinite()
                || value < 0;
    }
}