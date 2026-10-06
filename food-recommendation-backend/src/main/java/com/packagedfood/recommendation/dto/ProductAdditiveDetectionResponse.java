package com.packagedfood.recommendation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ProductAdditiveDetectionResponse {

    private Long productId;

    private String productName;

    private String ingredientsText;

    private int detectedAdditiveCount;

    private List<DetectedAdditive> additives;

    @Getter
    @AllArgsConstructor
    public static class DetectedAdditive {

        private Long additiveId;

        private String code;

        private String name;

        private String category;

        private String detectionMethod;

        private Double detectedAmount;

        private String detectedUnit;

        private String thresholdType;

        private Double thresholdValue;

        private String thresholdUnit;

        private String foodCategory;

        private String jurisdiction;

        private String source;

        private String sourceUrl;
    }
}