package com.packagedfood.recommendation.controller;

import com.packagedfood.recommendation.dto.AiPredictionResponse;
import com.packagedfood.recommendation.dto.ProductAnalysisRequest;
import com.packagedfood.recommendation.service.ProductAnalysisService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
public class ProductAnalysisController {

    private final ProductAnalysisService productAnalysisService;

    public ProductAnalysisController(
            ProductAnalysisService productAnalysisService) {

        this.productAnalysisService =
                productAnalysisService;
    }

    @PostMapping("/analyze")
    public ResponseEntity<AiPredictionResponse>
    analyzeProduct(
            @RequestBody ProductAnalysisRequest request) {

        AiPredictionResponse response =
                productAnalysisService.analyzeProduct(
                        request
                );

        return ResponseEntity.ok(response);
    }
}