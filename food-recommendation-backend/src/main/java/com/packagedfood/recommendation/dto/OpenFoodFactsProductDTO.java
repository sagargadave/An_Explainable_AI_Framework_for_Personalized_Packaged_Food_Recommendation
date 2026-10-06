package com.packagedfood.recommendation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OpenFoodFactsProductDTO {

    private String code;

    private String product_name;

    private String brands;

    private String categories;

    private String ingredients_text;

    private String image_url;

    private Double energy_100g;

    private Double fat_100g;

    @JsonProperty("saturated-fat_100g")
    private Double saturated_fat_100g;

    private Double carbohydrates_100g;

    private Double sugars_100g;

    private Double fiber_100g;

    private Double proteins_100g;

    private Double salt_100g;
}