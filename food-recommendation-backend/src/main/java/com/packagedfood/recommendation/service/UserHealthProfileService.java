package com.packagedfood.recommendation.service;

import com.packagedfood.recommendation.dto.UserHealthProfileRequest;
import com.packagedfood.recommendation.dto.UserHealthProfileResponse;
import com.packagedfood.recommendation.entity.HealthCondition;
import com.packagedfood.recommendation.entity.UserHealthProfile;
import com.packagedfood.recommendation.repository.HealthConditionRepository;
import com.packagedfood.recommendation.repository.UserHealthProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserHealthProfileService {

    private final UserHealthProfileRepository profileRepository;
    private final HealthConditionRepository healthConditionRepository;

    public UserHealthProfileService(
            UserHealthProfileRepository profileRepository,
            HealthConditionRepository healthConditionRepository) {

        this.profileRepository = profileRepository;
        this.healthConditionRepository = healthConditionRepository;
    }

    @Transactional
    public UserHealthProfileResponse createProfile(
            UserHealthProfileRequest request) {

        validateRequest(request);

        List<HealthCondition> conditions =
                healthConditionRepository.findAllById(
                        request.getHealthConditionIds()
                );

        if (conditions.size()
                != request.getHealthConditionIds().size()) {

            throw new IllegalArgumentException(
                    "One or more health condition IDs are invalid"
            );
        }

        Set<HealthCondition> conditionSet =
                new HashSet<>(conditions);

        UserHealthProfile profile =
                new UserHealthProfile(
                        request.getAge(),
                        request.getWeightKg(),
                        conditionSet
                );

        UserHealthProfile saved =
                profileRepository.save(profile);

        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public UserHealthProfileResponse getProfile(Long id) {

        UserHealthProfile profile =
                profileRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Health profile not found with id: "
                                                + id
                                ));

        return toResponse(profile);
    }

    private void validateRequest(
            UserHealthProfileRequest request) {

        if (request == null) {
            throw new IllegalArgumentException(
                    "Request body is required"
            );
        }

        if (request.getAge() == null
                || request.getAge() <= 0
                || request.getAge() > 120) {

            throw new IllegalArgumentException(
                    "Age must be between 1 and 120"
            );
        }

        if (request.getWeightKg() == null
                || request.getWeightKg() <= 0
                || request.getWeightKg() > 500) {

            throw new IllegalArgumentException(
                    "Weight must be greater than 0 and at most 500 kg"
            );
        }

        if (request.getHealthConditionIds() == null
                || request.getHealthConditionIds().isEmpty()) {

            throw new IllegalArgumentException(
                    "At least one health condition must be selected"
            );
        }
    }

    private UserHealthProfileResponse toResponse(
            UserHealthProfile profile) {

        List<UserHealthProfileResponse.HealthConditionSummary>
                conditions =
                profile.getHealthConditions()
                        .stream()
                        .map(condition ->
                                new UserHealthProfileResponse
                                        .HealthConditionSummary(
                                        condition.getId(),
                                        condition.getName()
                                )
                        )
                        .toList();

        return new UserHealthProfileResponse(
                profile.getId(),
                profile.getAge(),
                profile.getWeightKg(),
                conditions
        );
    }
}