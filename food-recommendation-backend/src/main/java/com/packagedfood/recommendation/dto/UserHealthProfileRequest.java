package com.packagedfood.recommendation.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class UserHealthProfileRequest {

    private Integer age;

    private Double weightKg;

    private List<Long> healthConditionIds;
}