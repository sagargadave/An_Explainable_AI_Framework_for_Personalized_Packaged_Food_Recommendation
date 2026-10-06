package com.packagedfood.recommendation.controller;

import com.packagedfood.recommendation.dto.AdditiveRiskRequest;
import com.packagedfood.recommendation.dto.AdditiveRiskResponse;
import com.packagedfood.recommendation.service.AdditiveRiskAnalysisService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/additive-risk")
public class AdditiveRiskAnalysisController {

    private final AdditiveRiskAnalysisService
            additiveRiskAnalysisService;

    public AdditiveRiskAnalysisController(
            AdditiveRiskAnalysisService additiveRiskAnalysisService) {

        this.additiveRiskAnalysisService =
                additiveRiskAnalysisService;
    }

    @PostMapping("/analyze")
    public ResponseEntity<AdditiveRiskResponse> analyze(
            @RequestBody AdditiveRiskRequest request) {

        return ResponseEntity.ok(
                additiveRiskAnalysisService.analyze(request)
        );
    }
}