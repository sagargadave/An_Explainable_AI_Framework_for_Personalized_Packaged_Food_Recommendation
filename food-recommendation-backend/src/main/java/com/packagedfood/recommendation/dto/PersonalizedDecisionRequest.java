package com.packagedfood.recommendation.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PersonalizedDecisionRequest {

    private Long productId;

    private Long healthProfileId;
}