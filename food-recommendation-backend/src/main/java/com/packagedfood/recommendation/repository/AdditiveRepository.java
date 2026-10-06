package com.packagedfood.recommendation.repository;

import com.packagedfood.recommendation.entity.Additive;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdditiveRepository
        extends JpaRepository<Additive, Long> {

    Optional<Additive> findByCodeIgnoreCase(String code);

    Optional<Additive> findByNameIgnoreCase(String name);
}