package com.packagedfood.recommendation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class UserHealthProfileResponse {

    private Long id;

    private Integer age;

    private Double weightKg;

    private List<HealthConditionSummary> healthConditions;

    @Getter
    @AllArgsConstructor
    public static class HealthConditionSummary {

        private Long id;

        private String name;
    }
}