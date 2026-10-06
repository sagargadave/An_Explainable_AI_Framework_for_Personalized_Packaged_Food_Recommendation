package com.packagedfood.recommendation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class AdditiveRiskResponse {

    private Long productId;

    private String productName;

    private Long healthProfileId;

    private Double userWeightKg;

    private int detectedAdditiveCount;

    private int assessableAdditiveCount;

    private int cannotAssessCount;

    private boolean avoidRecommended;

    private List<AdditiveRiskFinding> findings;

    @Getter
    @AllArgsConstructor
    public static class AdditiveRiskFinding {

        private Long additiveId;

        private String code;

        private String name;

        private String category;

        private String riskLevel;

        private String thresholdType;

        private Double thresholdValue;

        private String thresholdUnit;

        private Double detectedAmount;

        private String detectedUnit;

        private Double userWeightKg;

        private Double calculatedDailyLimit;

        private String riskStatus;

        private String recommendation;

        private String reason;

        private String source;

        private String sourceUrl;
    }
}