package com.packagedfood.recommendation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class PersonalizedDecisionResponse {

    private Long productId;
    private String productName;
    private Long healthProfileId;

    private String nutritionGrade;
    private String nutritionSummary;

    private String finalRecommendation;
    private String overallSeverity;

    private List<String> reasons;

    private DecisionSummary decisionSummary;

    @Getter
    @AllArgsConstructor
    public static class DecisionSummary {

        private boolean highHealthConcern;
        private boolean moderateHealthConcern;
        private boolean healthInformationAvailable;

        private boolean additiveAvoid;
        private boolean additiveCannotAssess;

        private boolean poorNutrition;
        private boolean nutritionAnalysisAvailable;
    }
}