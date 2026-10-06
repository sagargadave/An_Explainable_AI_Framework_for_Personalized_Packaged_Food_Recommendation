package com.packagedfood.recommendation.controller;

import com.packagedfood.recommendation.entity.HealthCondition;
import com.packagedfood.recommendation.entity.HealthConditionRule;
import com.packagedfood.recommendation.service.HealthConditionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/health-conditions")
public class HealthConditionController {

    private final HealthConditionService healthConditionService;

    public HealthConditionController(
            HealthConditionService healthConditionService) {

        this.healthConditionService = healthConditionService;
    }

    @GetMapping
    public List<HealthCondition> getAllConditions() {
        return healthConditionService.getAllConditions();
    }

    @GetMapping("/{id}")
    public HealthCondition getCondition(
            @PathVariable Long id) {

        return healthConditionService.getCondition(id);
    }

    @GetMapping("/{id}/rules")
    public List<HealthConditionRule> getRules(
            @PathVariable Long id) {

        return healthConditionService.getRules(id);
    }
}