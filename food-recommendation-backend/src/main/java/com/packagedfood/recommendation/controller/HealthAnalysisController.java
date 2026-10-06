package com.packagedfood.recommendation.controller;

import com.packagedfood.recommendation.dto.HealthAnalysisRequest;
import com.packagedfood.recommendation.dto.HealthAnalysisResponse;
import com.packagedfood.recommendation.service.HealthAnalysisService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/health-analysis")
public class HealthAnalysisController {

    private final HealthAnalysisService healthAnalysisService;

    public HealthAnalysisController(
            HealthAnalysisService healthAnalysisService) {

        this.healthAnalysisService =
                healthAnalysisService;
    }

    @PostMapping("/analyze")
    public ResponseEntity<HealthAnalysisResponse> analyze(
            @RequestBody HealthAnalysisRequest request) {

        return ResponseEntity.ok(
                healthAnalysisService.analyze(request)
        );
    }
}