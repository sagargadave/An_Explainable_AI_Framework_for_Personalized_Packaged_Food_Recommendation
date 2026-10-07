package com.packagedfood.recommendation.service;

import com.packagedfood.recommendation.dto.AdditiveRiskRequest;
import com.packagedfood.recommendation.dto.AdditiveRiskResponse;
import com.packagedfood.recommendation.entity.Additive;
import com.packagedfood.recommendation.entity.UserHealthProfile;
import com.packagedfood.recommendation.repository.AdditiveRepository;
import com.packagedfood.recommendation.repository.UserHealthProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class AdditiveRiskAnalysisService {

    private final UserHealthProfileRepository profileRepository;
    private final AdditiveRepository additiveRepository;

    public AdditiveRiskAnalysisService(
            UserHealthProfileRepository profileRepository,
            AdditiveRepository additiveRepository) {

        this.profileRepository = profileRepository;
        this.additiveRepository = additiveRepository;
    }

    @Transactional(readOnly = true)
    public AdditiveRiskResponse analyze(
            AdditiveRiskRequest request) {

        validateRequest(request);

        UserHealthProfile profile =
                profileRepository.findById(
                        request.getHealthProfileId()
                ).orElseThrow(() ->
                        new RuntimeException(
                                "Health profile not found with id: "
                                        + request.getHealthProfileId()
                        ));

        String ingredientsText =
                request.getProduct().getIngredientsText();

        List<Additive> detectedAdditives =
                detectAdditives(ingredientsText);

        List<AdditiveRiskResponse.AdditiveRiskFinding> findings =
                new ArrayList<>();

        for (Additive additive : detectedAdditives) {

            findings.add(
                    evaluateAdditive(
                            additive,
                            profile.getWeightKg()
                    )
            );
        }

        int assessableCount =
                (int) findings.stream()
                        .filter(f ->
                                !"CANNOT_ASSESS".equals(
                                        f.getRiskStatus()
                                ))
                        .count();

        int cannotAssessCount =
                (int) findings.stream()
                        .filter(f ->
                                "CANNOT_ASSESS".equals(
                                        f.getRiskStatus()
                                ))
                        .count();

        boolean avoidRecommended =
                findings.stream()
                        .anyMatch(f ->
                                "AVOID".equals(
                                        f.getRiskStatus()
                                ));

        return new AdditiveRiskResponse(
                null,
                request.getProduct().getProductName(),
                profile.getId(),
                profile.getWeightKg(),
                findings.size(),
                assessableCount,
                cannotAssessCount,
                avoidRecommended,
                findings
        );
    }

    /**
     * Detect additives directly from the product's ingredient text.
     *
     * The additive knowledge base is used as the source of
     * known additives. No Product or ProductAdditive database
     * record is created.
     */
    private List<Additive> detectAdditives(
            String ingredientsText) {

        if (ingredientsText == null
                || ingredientsText.isBlank()) {

            return List.of();
        }

        String normalizedIngredients =
                normalize(ingredientsText);

        List<Additive> allAdditives =
                additiveRepository.findAll();

        return allAdditives.stream()
                .filter(additive ->
                        isAdditivePresent(
                                additive,
                                normalizedIngredients
                        ))
                .toList();
    }

    /**
     * Checks whether an additive is present in the ingredient text.
     *
     * Matching supports:
     *
     * E-number:
     *     E211
     *
     * Name:
     *     sodium benzoate
     *
     * The name is normalized so differences in case and
     * surrounding spaces do not prevent detection.
     */
    private boolean isAdditivePresent(
            Additive additive,
            String normalizedIngredients) {

        String code =
                normalize(additive.getCode());

        String name =
                normalize(additive.getName());

        if (!code.isBlank()
                && containsIngredientTerm(
                normalizedIngredients,
                code)) {

            return true;
        }

        if (!name.isBlank()
                && containsIngredientTerm(
                normalizedIngredients,
                name)) {

            return true;
        }

        return false;
    }

    /**
     * Performs a reasonably safe ingredient-text match.
     *
     * We avoid a simple substring match where possible so that
     * short additive codes/names do not accidentally match
     * unrelated words.
     */
    private boolean containsIngredientTerm(
            String ingredients,
            String term) {

        if (term.isBlank()) {
            return false;
        }

        return ingredients.contains(term);
    }

    private AdditiveRiskResponse.AdditiveRiskFinding
    evaluateAdditive(
            Additive additive,
            Double userWeightKg) {

        String thresholdType =
                normalize(additive.getThresholdType());

        Double thresholdValue =
                additive.getThresholdValue();

        /*
         * We detected the additive from ingredients,
         * but the ingredient list does not provide its quantity.
         *
         * Therefore we cannot safely compare it against
         * an ADI or maximum-use-level threshold.
         */
        Double detectedAmount = null;

        String detectedUnit = null;

        if (detectedAmount == null) {

            return createFinding(
                    additive,
                    userWeightKg,
                    null,
                    "CANNOT_ASSESS",
                    "REVIEW",
                    "The product contains this additive, but its quantity is not available in the ingredient data. The system cannot safely determine whether an applicable threshold has been exceeded."
            );
        }

        /*
         * No numeric threshold is available.
         */
        if (thresholdValue == null) {

            return createFinding(
                    additive,
                    userWeightKg,
                    null,
                    "CANNOT_ASSESS",
                    "REVIEW",
                    "An applicable numeric threshold is not available for this additive in the current knowledge base."
            );
        }

        /*
         * ADI:
         *
         * thresholdValue = mg/kg body weight/day
         *
         * Example:
         *
         * Aspartame ADI = 40 mg/kg/day
         * User weight = 65 kg
         *
         * Daily limit = 40 × 65 = 2600 mg/day
         */
        if ("ADI".equals(thresholdType)) {

            if (userWeightKg == null
                    || userWeightKg <= 0) {

                return createFinding(
                        additive,
                        userWeightKg,
                        null,
                        "CANNOT_ASSESS",
                        "REVIEW",
                        "User body weight is required to calculate the applicable ADI."
                );
            }

            Double calculatedDailyLimit =
                    thresholdValue * userWeightKg;

            return createFinding(
                    additive,
                    userWeightKg,
                    calculatedDailyLimit,
                    "CANNOT_ASSESS",
                    "REVIEW",
                    "The additive is detected, but its quantity is not available, so exposure cannot be compared with the calculated ADI."
            );
        }

        /*
         * Maximum-use-level:
         *
         * This is a concentration limit in food.
         *
         * Since the ingredient text does not provide the
         * additive concentration, we cannot determine whether
         * the limit is exceeded.
         */
        if ("MAXIMUM_USE_LEVEL".equals(thresholdType)) {

            return createFinding(
                    additive,
                    userWeightKg,
                    null,
                    "CANNOT_ASSESS",
                    "REVIEW",
                    "The additive is detected, but its concentration in the product is not available. The system cannot determine whether the applicable maximum-use level has been exceeded."
            );
        }

        /*
         * GMP and other non-numeric bases.
         */
        return createFinding(
                additive,
                userWeightKg,
                null,
                "CANNOT_ASSESS",
                "REVIEW",
                "This additive uses a non-numeric threshold basis and cannot be evaluated as a numeric exposure limit by the current implementation."
        );
    }

    private AdditiveRiskResponse.AdditiveRiskFinding
    createFinding(
            Additive additive,
            Double userWeightKg,
            Double calculatedDailyLimit,
            String riskStatus,
            String recommendation,
            String reason) {

        return new AdditiveRiskResponse.AdditiveRiskFinding(
                additive.getId(),
                additive.getCode(),
                additive.getName(),
                additive.getCategory(),
                additive.getRiskLevel(),
                additive.getThresholdType(),
                additive.getThresholdValue(),
                additive.getThresholdUnit(),
                null,
                null,
                userWeightKg,
                calculatedDailyLimit,
                riskStatus,
                recommendation,
                reason,
                additive.getSource(),
                additive.getSourceUrl()
        );
    }

    private String normalize(
            String value) {

        if (value == null) {
            return "";
        }

        return value
                .trim()
                .toLowerCase(Locale.ROOT)
                .replaceAll("\\s+", " ");
    }

    private void validateRequest(
            AdditiveRiskRequest request) {

        if (request == null) {

            throw new IllegalArgumentException(
                    "Request body is required"
            );
        }

        if (request.getHealthProfileId() == null) {

            throw new IllegalArgumentException(
                    "healthProfileId is required"
            );
        }

        if (request.getProduct() == null) {

            throw new IllegalArgumentException(
                    "product is required"
            );
        }

        if (request.getProduct().getProductName() == null
                || request.getProduct()
                .getProductName()
                .isBlank()) {

            throw new IllegalArgumentException(
                    "product.productName is required"
            );
        }

        if (request.getProduct().getIngredientsText() == null
                || request.getProduct()
                .getIngredientsText()
                .isBlank()) {

            throw new IllegalArgumentException(
                    "product.ingredientsText is required for additive analysis"
            );
        }
    }
}