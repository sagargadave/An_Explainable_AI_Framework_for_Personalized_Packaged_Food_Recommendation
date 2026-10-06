package com.packagedfood.recommendation.controller;

import com.packagedfood.recommendation.entity.Additive;
import com.packagedfood.recommendation.service.AdditiveService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/additives")
public class AdditiveController {

    private final AdditiveService additiveService;

    public AdditiveController(AdditiveService additiveService) {
        this.additiveService = additiveService;
    }

    @GetMapping
    public List<Additive> getAllAdditives() {
        return additiveService.getAllAdditives();
    }

    @GetMapping("/{id}")
    public Additive getAdditive(
            @PathVariable Long id) {

        return additiveService.getAdditive(id);
    }
}