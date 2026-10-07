package com.packagedfood.recommendation.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AdditiveRiskRequest {

    private Long healthProfileId;

    private ProductData product;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class ProductData {

        private String barcode;

        private String productName;

        private String ingredientsText;
    }
}