package com.packagedfood.recommendation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AiPredictionRequest {

    private Double energy;
    private Double fat;
    private Double saturatedFat;
    private Double carbohydrates;
    private Double sugars;
    private Double fiber;
    private Double proteins;
    private Double salt;
}