package com.packagedfood.recommendation.repository;

import com.packagedfood.recommendation.entity.HealthConditionRule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HealthConditionRuleRepository
        extends JpaRepository<HealthConditionRule, Long> {

    List<HealthConditionRule> findByHealthConditionId(Long healthConditionId);
}