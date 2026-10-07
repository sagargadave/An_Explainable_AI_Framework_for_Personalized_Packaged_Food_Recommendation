package com.packagedfood.recommendation.controller;

import com.packagedfood.recommendation.dto.AdditiveRiskRequest;
import com.packagedfood.recommendation.dto.AdditiveRiskResponse;
import com.packagedfood.recommendation.entity.Additive;
import com.packagedfood.recommendation.service.AdditiveRiskAnalysisService;
import com.packagedfood.recommendation.service.AdditiveService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/additives")
public class AdditiveController {

    private final AdditiveService additiveService;
    private final AdditiveRiskAnalysisService additiveRiskAnalysisService;

    public AdditiveController(
            AdditiveService additiveService,
            AdditiveRiskAnalysisService additiveRiskAnalysisService) {

        this.additiveService = additiveService;
        this.additiveRiskAnalysisService = additiveRiskAnalysisService;
    }

    /**
     * Get all additives from the knowledge base.
     */
    @GetMapping
    public List<Additive> getAllAdditives() {
        return additiveService.getAllAdditives();
    }

    /**
     * Get a single additive by ID.
     */
    @GetMapping("/{id}")
    public Additive getAdditive(
            @PathVariable Long id) {

        return additiveService.getAdditive(id);
    }

    /**
     * Analyze additives present in a selected product.
     *
     * The product data is supplied directly in the request.
     * The product does not need to be stored in the database.
     */
    @PostMapping("/analyze")
    public ResponseEntity<AdditiveRiskResponse> analyzeAdditives(
            @RequestBody AdditiveRiskRequest request) {

        AdditiveRiskResponse response =
                additiveRiskAnalysisService.analyze(request);

        return ResponseEntity.ok(response);
    }
}