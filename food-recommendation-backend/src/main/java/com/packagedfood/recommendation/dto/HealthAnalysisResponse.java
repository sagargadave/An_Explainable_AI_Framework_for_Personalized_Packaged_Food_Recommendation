package com.packagedfood.recommendation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class HealthAnalysisResponse {

    private String productBarcode;

    private String productName;

    private Long healthProfileId;

    private List<ConditionAnalysis> conditions;

    private String overallRecommendation;

    @Getter
    @AllArgsConstructor
    public static class ConditionAnalysis {

        private Long healthConditionId;

        private String healthConditionName;

        private String overallSeverity;

        private String recommendation;

        private List<HealthFinding> findings;
    }

    @Getter
    @AllArgsConstructor
    public static class HealthFinding {

        private String factor;

        private String severity;

        private Double productValue;

        private String unit;

        private String ruleOperator;

        private Double thresholdValue;

        private String recommendation;

        private String reason;

        private String source;

        private String sourceUrl;
    }
}