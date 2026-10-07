package com.packagedfood.recommendation.service;

import com.packagedfood.recommendation.client.AiPredictionClient;
import com.packagedfood.recommendation.dto.AdditiveRiskRequest;
import com.packagedfood.recommendation.dto.AdditiveRiskResponse;
import com.packagedfood.recommendation.dto.AiPredictionRequest;
import com.packagedfood.recommendation.dto.AiPredictionResponse;
import com.packagedfood.recommendation.dto.HealthAnalysisRequest;
import com.packagedfood.recommendation.dto.HealthAnalysisResponse;
import com.packagedfood.recommendation.dto.PersonalizedDecisionRequest;
import com.packagedfood.recommendation.dto.PersonalizedDecisionResponse;
import com.packagedfood.recommendation.entity.Product;
import com.packagedfood.recommendation.entity.UserHealthProfile;
import com.packagedfood.recommendation.repository.ProductRepository;
import com.packagedfood.recommendation.repository.UserHealthProfileRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PersonalizedDecisionService {

    private final ProductRepository productRepository;
    private final UserHealthProfileRepository profileRepository;
    private final AiPredictionClient aiPredictionClient;
    private final HealthAnalysisService healthAnalysisService;
    private final AdditiveRiskAnalysisService additiveRiskAnalysisService;

    public PersonalizedDecisionService(
            ProductRepository productRepository,
            UserHealthProfileRepository profileRepository,
            AiPredictionClient aiPredictionClient,
            HealthAnalysisService healthAnalysisService,
            AdditiveRiskAnalysisService additiveRiskAnalysisService) {

        this.productRepository = productRepository;
        this.profileRepository = profileRepository;
        this.aiPredictionClient = aiPredictionClient;
        this.healthAnalysisService = healthAnalysisService;
        this.additiveRiskAnalysisService = additiveRiskAnalysisService;
    }

    public PersonalizedDecisionResponse analyze(
            PersonalizedDecisionRequest request) {

        validateRequest(request);

        Product product =
                productRepository.findById(
                        request.getProductId()
                ).orElseThrow(() ->
                        new IllegalArgumentException(
                                "Product not found with id: "
                                        + request.getProductId()
                        )
                );

        UserHealthProfile profile =
                profileRepository.findById(
                        request.getHealthProfileId()
                ).orElseThrow(() ->
                        new IllegalArgumentException(
                                "Health profile not found with id: "
                                        + request.getHealthProfileId()
                        )
                );

        /*
         * ---------------------------------------------------------
         * 1. NUTRITION / ML ANALYSIS
         * ---------------------------------------------------------
         *
         * Sends the product's eight nutrition features
         * to the existing Python/XGBoost AI service.
         */
        AiPredictionResponse nutritionAnalysis =
                analyzeNutrition(product);

        /*
         * ---------------------------------------------------------
         * 2. HEALTH-CONDITION ANALYSIS
         * ---------------------------------------------------------
         *
         * The health analysis receives:
         *
         * - healthProfileId
         * - product data directly
         *
         * The product does not need to be loaded again
         * inside HealthAnalysisService.
         */
        HealthAnalysisResponse healthAnalysis =
                analyzeHealth(product, profile);

        /*
         * ---------------------------------------------------------
         * 3. ADDITIVE-RISK ANALYSIS
         * ---------------------------------------------------------
         *
         * The additive analysis also receives:
         *
         * - healthProfileId
         * - product data directly
         *
         * The selected Open Food Facts product is therefore
         * not required to be stored in the database.
         */
        AdditiveRiskResponse additiveRisk =
                analyzeAdditives(product, profile);

        /*
         * ---------------------------------------------------------
         * 4. COMBINE ALL THREE ANALYSES
         * ---------------------------------------------------------
         */
        return buildFinalDecision(
                product,
                profile,
                nutritionAnalysis,
                healthAnalysis,
                additiveRisk
        );
    }

    /**
     * Sends the product's eight nutrition features
     * to the existing Python/XGBoost AI service.
     */
    private AiPredictionResponse analyzeNutrition(
            Product product) {

        if (!hasCompleteNutritionData(product)) {
            return null;
        }

        AiPredictionRequest request =
                new AiPredictionRequest();

        request.setEnergy(
                product.getEnergy100g()
        );

        request.setFat(
                product.getFat100g()
        );

        request.setSaturatedFat(
                product.getSaturatedFat100g()
        );

        request.setCarbohydrates(
                product.getCarbohydrates100g()
        );

        request.setSugars(
                product.getSugars100g()
        );

        request.setFiber(
                product.getFiber100g()
        );

        request.setProteins(
                product.getProteins100g()
        );

        request.setSalt(
                product.getSalt100g()
        );

        return aiPredictionClient.predict(request);
    }

    /**
     * Uses the health profile and the selected product
     * directly for health-condition analysis.
     *
     * The product is NOT stored by this method.
     */
    private HealthAnalysisResponse analyzeHealth(
            Product product,
            UserHealthProfile profile) {

        HealthAnalysisRequest request =
                new HealthAnalysisRequest();

        /*
         * Use the existing health profile.
         */
        request.setHealthProfileId(
                profile.getId()
        );

        /*
         * Build product data directly from
         * the selected product.
         */
        HealthAnalysisRequest.ProductData productData =
                new HealthAnalysisRequest.ProductData();

        productData.setBarcode(
                product.getBarcode()
        );

        productData.setProductName(
                product.getProductName()
        );

        productData.setIngredientsText(
                product.getIngredientsText()
        );

        productData.setEnergy100g(
                product.getEnergy100g()
        );

        productData.setFat100g(
                product.getFat100g()
        );

        productData.setSaturatedFat100g(
                product.getSaturatedFat100g()
        );

        productData.setCarbohydrates100g(
                product.getCarbohydrates100g()
        );

        productData.setSugars100g(
                product.getSugars100g()
        );

        productData.setFiber100g(
                product.getFiber100g()
        );

        productData.setProteins100g(
                product.getProteins100g()
        );

        productData.setSalt100g(
                product.getSalt100g()
        );

        request.setProduct(
                productData
        );

        return healthAnalysisService.analyze(request);
    }

    /**
     * Performs additive-risk analysis using
     * the selected product data directly.
     *
     * The product is NOT required to exist in the
     * product database.
     */
    private AdditiveRiskResponse analyzeAdditives(
            Product product,
            UserHealthProfile profile) {

        AdditiveRiskRequest request =
                new AdditiveRiskRequest();

        /*
         * Use the existing health profile.
         */
        request.setHealthProfileId(
                profile.getId()
        );

        /*
         * Build product data directly from
         * the selected product.
         */
        AdditiveRiskRequest.ProductData productData =
                new AdditiveRiskRequest.ProductData();

        productData.setBarcode(
                product.getBarcode()
        );

        productData.setProductName(
                product.getProductName()
        );

        productData.setIngredientsText(
                product.getIngredientsText()
        );

        request.setProduct(
                productData
        );

        return additiveRiskAnalysisService.analyze(
                request
        );
    }

    /**
     * Combines ML, health-condition and additive-risk
     * results into one personalized recommendation.
     */
    private PersonalizedDecisionResponse buildFinalDecision(
            Product product,
            UserHealthProfile profile,
            AiPredictionResponse nutritionAnalysis,
            HealthAnalysisResponse healthAnalysis,
            AdditiveRiskResponse additiveRisk) {

        List<String> reasons =
                new ArrayList<>();

        /*
         * ---------------------------------------------------------
         * HEALTH ANALYSIS
         * ---------------------------------------------------------
         */

        boolean highHealthConcern =
                healthAnalysis.getConditions()
                        .stream()
                        .anyMatch(condition ->
                                "HIGH".equalsIgnoreCase(
                                        condition.getOverallSeverity()
                                )
                        );

        boolean moderateHealthConcern =
                healthAnalysis.getConditions()
                        .stream()
                        .anyMatch(condition ->
                                "MODERATE".equalsIgnoreCase(
                                        condition.getOverallSeverity()
                                )
                        );

        /*
         * ---------------------------------------------------------
         * ADDITIVE ANALYSIS
         * ---------------------------------------------------------
         */

        boolean additiveAvoid =
                additiveRisk.isAvoidRecommended();

        boolean additiveCannotAssess =
                additiveRisk.getCannotAssessCount() > 0;

        /*
         * ---------------------------------------------------------
         * NUTRITION ANALYSIS
         * ---------------------------------------------------------
         */

        boolean nutritionAnalysisAvailable =
                nutritionAnalysis != null;

        String nutritionGrade =
                nutritionAnalysisAvailable
                        ? nutritionAnalysis.getPrediction()
                        : null;

        boolean poorNutrition =
                isPoorNutritionGrade(
                        nutritionGrade
                );

        /*
         * ---------------------------------------------------------
         * BUILD EXPLANATION
         * ---------------------------------------------------------
         */

        if (highHealthConcern) {

            reasons.add(
                    "A high-severity health concern was identified for the selected health profile."
            );
        }

        if (moderateHealthConcern) {

            reasons.add(
                    "A moderate health concern was identified for the selected health profile."
            );
        }

        if (additiveAvoid) {

            reasons.add(
                    "At least one detected additive exceeds an applicable threshold."
            );
        }

        if (additiveCannotAssess) {

            reasons.add(
                    "One or more detected additives could not be quantitatively assessed because the required amount or applicable numeric threshold is unavailable."
            );
        }

        if (poorNutrition) {

            reasons.add(
                    "The product received a relatively poor predicted Nutri-Score."
            );
        }

        if (!nutritionAnalysisAvailable) {

            reasons.add(
                    "Nutrition analysis could not be performed because complete nutrition data is unavailable."
            );
        }

        /*
         * ---------------------------------------------------------
         * FINAL DECISION
         * ---------------------------------------------------------
         *
         * Priority:
         *
         * 1. AVOID
         * 2. CAUTION
         * 3. REVIEW
         * 4. SUITABLE
         */

        String finalRecommendation;
        String overallSeverity;

        if (highHealthConcern || additiveAvoid) {

            finalRecommendation = "AVOID";
            overallSeverity = "HIGH";

        } else if (moderateHealthConcern || poorNutrition) {

            finalRecommendation = "CAUTION";
            overallSeverity = "MODERATE";

        } else if (additiveCannotAssess
                || !nutritionAnalysisAvailable) {

            finalRecommendation = "REVIEW";
            overallSeverity = "UNKNOWN";

        } else {

            finalRecommendation = "SUITABLE";
            overallSeverity = "LOW";
        }

        /*
         * If nothing produced a concern,
         * provide a neutral explanation.
         */
        if (reasons.isEmpty()) {

            reasons.add(
                    "No configured health, additive, or nutrition concern was triggered by the available data."
            );
        }

        /*
         * ---------------------------------------------------------
         * DECISION SUMMARY
         * ---------------------------------------------------------
         */

        PersonalizedDecisionResponse.DecisionSummary summary =
                new PersonalizedDecisionResponse.DecisionSummary(
                        highHealthConcern,
                        moderateHealthConcern,
                        additiveAvoid,
                        additiveCannotAssess,
                        poorNutrition,
                        nutritionAnalysisAvailable
                );

        /*
         * ---------------------------------------------------------
         * FINAL RESPONSE
         * ---------------------------------------------------------
         */

        return new PersonalizedDecisionResponse(
                product.getId(),
                product.getProductName(),
                profile.getId(),
                nutritionGrade,
                createNutritionSummary(
                        nutritionAnalysis
                ),
                finalRecommendation,
                overallSeverity,
                reasons,
                summary
        );
    }

    /**
     * For the current project, D and E are treated as
     * relatively poor Nutri-Score results.
     *
     * This contributes to CAUTION;
     * it does not independently produce AVOID.
     */
    private boolean isPoorNutritionGrade(
            String nutritionGrade) {

        if (nutritionGrade == null) {
            return false;
        }

        return "D".equalsIgnoreCase(
                nutritionGrade
        )
                || "E".equalsIgnoreCase(
                nutritionGrade
        );
    }

    /**
     * Creates a simple human-readable nutrition summary.
     */
    private String createNutritionSummary(
            AiPredictionResponse nutritionAnalysis) {

        if (nutritionAnalysis == null) {

            return "Nutrition analysis unavailable.";
        }

        return "Predicted Nutri-Score: "
                + nutritionAnalysis.getPrediction();
    }

    /**
     * The XGBoost model requires all eight nutrition features.
     */
    private boolean hasCompleteNutritionData(
            Product product) {

        return isValid(
                product.getEnergy100g()
        )
                && isValid(
                product.getFat100g()
        )
                && isValid(
                product.getSaturatedFat100g()
        )
                && isValid(
                product.getCarbohydrates100g()
        )
                && isValid(
                product.getSugars100g()
        )
                && isValid(
                product.getFiber100g()
        )
                && isValid(
                product.getProteins100g()
        )
                && isValid(
                product.getSalt100g()
        );
    }

    private boolean isValid(
            Double value) {

        return value != null
                && !value.isNaN()
                && !value.isInfinite()
                && value >= 0;
    }

    /**
     * Validates the personalized-decision request.
     */
    private void validateRequest(
            PersonalizedDecisionRequest request) {

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