package com.packagedfood.recommendation.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class HealthAnalysisRequest {

    private Long productId;

    private List<Long> healthConditionIds;
}