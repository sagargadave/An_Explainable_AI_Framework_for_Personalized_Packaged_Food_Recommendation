package com.packagedfood.recommendation.controller;

import com.packagedfood.recommendation.dto.PersonalizedDecisionRequest;
import com.packagedfood.recommendation.dto.PersonalizedDecisionResponse;
import com.packagedfood.recommendation.service.PersonalizedDecisionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/personalized-decision")
public class PersonalizedDecisionController {

    private final PersonalizedDecisionService
            personalizedDecisionService;

    public PersonalizedDecisionController(
            PersonalizedDecisionService personalizedDecisionService) {

        this.personalizedDecisionService =
                personalizedDecisionService;
    }

    @PostMapping("/analyze")
    public ResponseEntity<PersonalizedDecisionResponse> analyze(
            @RequestBody PersonalizedDecisionRequest request) {

        return ResponseEntity.ok(
                personalizedDecisionService.analyze(request)
        );
    }
}