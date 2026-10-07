package com.packagedfood.recommendation.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class HealthAnalysisRequest {

    private Long healthProfileId;

    private ProductData product;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class ProductData {

        private String barcode;

        private String productName;

        private String ingredientsText;

        private Double energy100g;

        private Double fat100g;

        private Double saturatedFat100g;

        private Double carbohydrates100g;

        private Double sugars100g;

        private Double fiber100g;

        private Double proteins100g;

        private Double salt100g;
    }
}