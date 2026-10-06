package com.packagedfood.recommendation.repository;

import com.packagedfood.recommendation.entity.HealthCondition;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HealthConditionRepository
        extends JpaRepository<HealthCondition, Long> {

    Optional<HealthCondition> findByNameIgnoreCase(String name);
}