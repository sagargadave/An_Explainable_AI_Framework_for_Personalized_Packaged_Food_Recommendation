package com.packagedfood.recommendation.controller;

import com.packagedfood.recommendation.dto.ProductAdditiveDetectionResponse;
import com.packagedfood.recommendation.service.ProductAdditiveDetectionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
public class ProductAdditiveDetectionController {

    private final ProductAdditiveDetectionService
            productAdditiveDetectionService;

    public ProductAdditiveDetectionController(
            ProductAdditiveDetectionService productAdditiveDetectionService) {

        this.productAdditiveDetectionService =
                productAdditiveDetectionService;
    }

    @PostMapping("/{productId}/additives/analyze")
    public ResponseEntity<ProductAdditiveDetectionResponse>
    analyzeAdditives(
            @PathVariable Long productId) {

        return ResponseEntity.ok(
                productAdditiveDetectionService
                        .detectAdditives(productId)
        );
    }

    @GetMapping("/{productId}/additives")
    public ResponseEntity<ProductAdditiveDetectionResponse>
    getDetectedAdditives(
            @PathVariable Long productId) {

        return ResponseEntity.ok(
                productAdditiveDetectionService
                        .getDetectedAdditives(productId)
        );
    }
}