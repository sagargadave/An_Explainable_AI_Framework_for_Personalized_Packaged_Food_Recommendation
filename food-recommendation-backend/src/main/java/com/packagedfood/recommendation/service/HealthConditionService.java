package com.packagedfood.recommendation.service;

import com.packagedfood.recommendation.entity.HealthCondition;
import com.packagedfood.recommendation.entity.HealthConditionRule;
import com.packagedfood.recommendation.repository.HealthConditionRepository;
import com.packagedfood.recommendation.repository.HealthConditionRuleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HealthConditionService {

    private final HealthConditionRepository healthConditionRepository;
    private final HealthConditionRuleRepository ruleRepository;

    public HealthConditionService(
            HealthConditionRepository healthConditionRepository,
            HealthConditionRuleRepository ruleRepository) {

        this.healthConditionRepository = healthConditionRepository;
        this.ruleRepository = ruleRepository;
    }

    public List<HealthCondition> getAllConditions() {
        return healthConditionRepository.findAll();
    }

    public HealthCondition getCondition(Long id) {
        return healthConditionRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Health condition not found"));
    }

    public List<HealthConditionRule> getRules(Long conditionId) {

        if (!healthConditionRepository.existsById(conditionId)) {
            throw new RuntimeException("Health condition not found");
        }

        return ruleRepository.findByHealthConditionId(conditionId);
    }
}