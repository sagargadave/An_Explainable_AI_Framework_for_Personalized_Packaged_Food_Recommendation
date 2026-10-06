package com.packagedfood.recommendation.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class AiPredictionResponse {

    private String prediction;

    private Integer predictionIndex;

    private Map<String, Double> probabilities;

    private List<Explanation> explanation;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Explanation {

        private String feature;

        private Double inputValue;

        private Double shapValue;

        private Double absoluteImpact;

        private String impact;
    }
}