package com.packagedfood.recommendation.service;

import com.packagedfood.recommendation.entity.Additive;
import com.packagedfood.recommendation.repository.AdditiveRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdditiveService {

    private final AdditiveRepository additiveRepository;

    public AdditiveService(AdditiveRepository additiveRepository) {
        this.additiveRepository = additiveRepository;
    }

    public List<Additive> getAllAdditives() {
        return additiveRepository.findAll();
    }

    public Additive getAdditive(Long id) {
        return additiveRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Additive not found"));
    }
}