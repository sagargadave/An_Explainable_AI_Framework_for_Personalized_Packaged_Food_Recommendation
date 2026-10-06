package com.packagedfood.recommendation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductAnalysisRequest {

    private Double energy100g;

    private Double fat100g;

    private Double saturatedFat100g;

    private Double carbohydrates100g;

    private Double sugars100g;

    private Double fiber100g;

    private Double proteins100g;

    private Double salt100g;
}