package com.packagedfood.recommendation.service;

import com.packagedfood.recommendation.dto.AdditiveRiskRequest;
import com.packagedfood.recommendation.dto.AdditiveRiskResponse;
import com.packagedfood.recommendation.entity.Additive;
import com.packagedfood.recommendation.entity.Product;
import com.packagedfood.recommendation.entity.ProductAdditive;
import com.packagedfood.recommendation.entity.UserHealthProfile;
import com.packagedfood.recommendation.repository.ProductAdditiveRepository;
import com.packagedfood.recommendation.repository.ProductRepository;
import com.packagedfood.recommendation.repository.UserHealthProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class AdditiveRiskAnalysisService {

    private final ProductRepository productRepository;
    private final UserHealthProfileRepository profileRepository;
    private final ProductAdditiveRepository productAdditiveRepository;

    public AdditiveRiskAnalysisService(
            ProductRepository productRepository,
            UserHealthProfileRepository profileRepository,
            ProductAdditiveRepository productAdditiveRepository) {

        this.productRepository = productRepository;
        this.profileRepository = profileRepository;
        this.productAdditiveRepository = productAdditiveRepository;
    }

    @Transactional(readOnly = true)
    public AdditiveRiskResponse analyze(
            AdditiveRiskRequest request) {

        validateRequest(request);

        Product product =
                productRepository.findById(request.getProductId())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Product not found with id: "
                                                + request.getProductId()
                                ));

        UserHealthProfile profile =
                profileRepository.findById(
                                request.getHealthProfileId())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Health profile not found with id: "
                                                + request.getHealthProfileId()
                                ));

        List<ProductAdditive> detectedAdditives =
                productAdditiveRepository.findByProductId(
                        product.getId()
                );

        List<AdditiveRiskResponse.AdditiveRiskFinding> findings =
                new ArrayList<>();

        for (ProductAdditive productAdditive :
                detectedAdditives) {

            findings.add(
                    evaluateAdditive(
                            productAdditive,
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
                product.getId(),
                product.getProductName(),
                profile.getId(),
                profile.getWeightKg(),
                findings.size(),
                assessableCount,
                cannotAssessCount,
                avoidRecommended,
                findings
        );
    }

    private AdditiveRiskResponse.AdditiveRiskFinding evaluateAdditive(
            ProductAdditive productAdditive,
            Double userWeightKg) {

        Additive additive =
                productAdditive.getAdditive();

        String thresholdType =
                normalize(additive.getThresholdType());

        Double thresholdValue =
                additive.getThresholdValue();

        Double detectedAmount =
                productAdditive.getDetectedAmount();

        String detectedUnit =
                productAdditive.getDetectedUnit();

        /*
         * The additive is detected, but its quantity is unknown.
         * Therefore exposure cannot be calculated safely.
         */
        if (detectedAmount == null) {

            return createFinding(
                    productAdditive,
                    userWeightKg,
                    null,
                    "CANNOT_ASSESS",
                    "REVIEW",
                    "The product contains this additive, but its quantity is not available. The system cannot safely determine whether an applicable threshold has been exceeded."
            );
        }

        /*
         * The knowledge base does not contain a numeric threshold.
         */
        if (thresholdValue == null) {

            return createFinding(
                    productAdditive,
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
         * ADI is expressed as mg/kg body weight/day.
         *
         * Example:
         *
         * ADI = 40 mg/kg bw/day
         * Weight = 65 kg
         *
         * Daily limit = 40 × 65 = 2600 mg/day
         */
        if ("ADI".equals(thresholdType)) {

            if (userWeightKg == null
                    || userWeightKg <= 0) {

                return createFinding(
                        productAdditive,
                        userWeightKg,
                        null,
                        "CANNOT_ASSESS",
                        "REVIEW",
                        "User body weight is required to calculate the applicable ADI."
                );
            }

            Double calculatedDailyLimit =
                    thresholdValue * userWeightKg;

            /*
             * The detected amount must be expressed in mg
             * for the current ADI comparison.
             */
            if (!isMilligramUnit(detectedUnit)) {

                return createFinding(
                        productAdditive,
                        userWeightKg,
                        calculatedDailyLimit,
                        "CANNOT_ASSESS",
                        "REVIEW",
                        "The detected additive quantity is not expressed in mg, so it cannot be directly compared with the calculated ADI."
                );
            }

            if (detectedAmount > calculatedDailyLimit) {

                return createFinding(
                        productAdditive,
                        userWeightKg,
                        calculatedDailyLimit,
                        "AVOID",
                        "AVOID",
                        "The detected additive amount exceeds the calculated daily ADI for the supplied body weight."
                );
            }

            return createFinding(
                    productAdditive,
                    userWeightKg,
                    calculatedDailyLimit,
                    "WITHIN_LIMIT",
                    "NO_SPECIFIC_CONCERN",
                    "The detected additive amount does not exceed the calculated daily ADI for the supplied body weight."
            );
        }

        /*
         * Maximum-use-level:
         *
         * This is a concentration limit in the food.
         * It is NOT the same thing as ADI.
         *
         * Therefore we compare only when the detected amount
         * and threshold use compatible units.
         */
        if ("MAXIMUM_USE_LEVEL".equals(thresholdType)) {

            if (!isCompatibleUnit(
                    detectedUnit,
                    additive.getThresholdUnit())) {

                return createFinding(
                        productAdditive,
                        userWeightKg,
                        null,
                        "CANNOT_ASSESS",
                        "REVIEW",
                        "The detected additive quantity cannot be directly compared with the stored maximum-use-level unit."
                );
            }

            if (detectedAmount > thresholdValue) {

                return createFinding(
                        productAdditive,
                        userWeightKg,
                        null,
                        "AVOID",
                        "AVOID",
                        "The detected additive concentration exceeds the stored maximum-use level."
                );
            }

            return createFinding(
                    productAdditive,
                    userWeightKg,
                    null,
                    "WITHIN_LIMIT",
                    "NO_SPECIFIC_CONCERN",
                    "The detected additive concentration does not exceed the stored maximum-use level."
            );
        }

        /*
         * GMP and other non-numeric threshold bases cannot be
         * evaluated using the numeric comparison implemented here.
         */
        return createFinding(
                productAdditive,
                userWeightKg,
                null,
                "CANNOT_ASSESS",
                "REVIEW",
                "This additive uses a non-numeric threshold basis and cannot be evaluated as a numeric exposure limit by the current implementation."
        );
    }

    private AdditiveRiskResponse.AdditiveRiskFinding createFinding(
            ProductAdditive productAdditive,
            Double userWeightKg,
            Double calculatedDailyLimit,
            String riskStatus,
            String recommendation,
            String reason) {

        Additive additive =
                productAdditive.getAdditive();

        return new AdditiveRiskResponse.AdditiveRiskFinding(
                additive.getId(),
                additive.getCode(),
                additive.getName(),
                additive.getCategory(),
                additive.getRiskLevel(),
                additive.getThresholdType(),
                additive.getThresholdValue(),
                additive.getThresholdUnit(),
                productAdditive.getDetectedAmount(),
                productAdditive.getDetectedUnit(),
                userWeightKg,
                calculatedDailyLimit,
                riskStatus,
                recommendation,
                reason,
                additive.getSource(),
                additive.getSourceUrl()
        );
    }

    private boolean isMilligramUnit(
            String unit) {

        if (unit == null) {
            return false;
        }

        String normalized =
                normalize(unit);

        return normalized.equals("MG")
                || normalized.equals("MILLIGRAM")
                || normalized.equals("MILLIGRAMS");
    }

    private boolean isCompatibleUnit(
            String detectedUnit,
            String thresholdUnit) {

        if (detectedUnit == null
                || thresholdUnit == null) {

            return false;
        }

        return normalize(detectedUnit)
                .equals(normalize(thresholdUnit));
    }

    private String normalize(
            String value) {

        if (value == null) {
            return "";
        }

        return value
                .trim()
                .toUpperCase(Locale.ROOT);
    }

    private void validateRequest(
            AdditiveRiskRequest request) {

        if (request == null) {

            throw new IllegalArgumentException(
                    "Request body is required"
            );
        }

        if (request.getProductId() == null) {

            throw new IllegalArgumentException(
                    "productId is required"
            );
        }

        if (request.getHealthProfileId() == null) {

            throw new IllegalArgumentException(
                    "healthProfileId is required"
            );
        }
    }
}