package com.packagedfood.recommendation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OpenFoodFactsProductDTO {

    private String code;

    private String product_name;

    private String brands;

    private String categories;

    private String ingredients_text;

    private String image_url;

    /*
     * Additive information from Open Food Facts.
     *
     * additives:
     * Human-readable additive information.
     *
     * additives_tags:
     * Structured additive tags such as en:e951.
     */
    private String additives;

    private List<String> additives_tags;

    /*
     * Serving information.
     *
     * IMPORTANT:
     * serving_quantity is the quantity of the complete food serving,
     * NOT the quantity of an individual additive.
     */
    private String serving_size;

    private Double serving_quantity;

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