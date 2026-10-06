package com.packagedfood.recommendation.controller;

import com.packagedfood.recommendation.dto.UserHealthProfileRequest;
import com.packagedfood.recommendation.dto.UserHealthProfileResponse;
import com.packagedfood.recommendation.service.UserHealthProfileService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/health-profiles")
public class UserHealthProfileController {

    private final UserHealthProfileService profileService;

    public UserHealthProfileController(
            UserHealthProfileService profileService) {

        this.profileService = profileService;
    }

    @PostMapping
    public ResponseEntity<UserHealthProfileResponse> createProfile(
            @RequestBody UserHealthProfileRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(profileService.createProfile(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserHealthProfileResponse> getProfile(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                profileService.getProfile(id)
        );
    }
}