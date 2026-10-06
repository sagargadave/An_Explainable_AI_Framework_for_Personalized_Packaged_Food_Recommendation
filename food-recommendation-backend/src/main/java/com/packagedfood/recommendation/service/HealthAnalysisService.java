package com.packagedfood.recommendation.service;

import com.packagedfood.recommendation.dto.HealthAnalysisRequest;
import com.packagedfood.recommendation.dto.HealthAnalysisResponse;
import com.packagedfood.recommendation.entity.HealthCondition;
import com.packagedfood.recommendation.entity.HealthConditionRule;
import com.packagedfood.recommendation.entity.Product;
import com.packagedfood.recommendation.repository.HealthConditionRepository;
import com.packagedfood.recommendation.repository.HealthConditionRuleRepository;
import com.packagedfood.recommendation.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@Service
public class HealthAnalysisService {

    private final ProductRepository productRepository;
    private final HealthConditionRepository healthConditionRepository;
    private final HealthConditionRuleRepository ruleRepository;

    public HealthAnalysisService(
            ProductRepository productRepository,
            HealthConditionRepository healthConditionRepository,
            HealthConditionRuleRepository ruleRepository) {

        this.productRepository = productRepository;
        this.healthConditionRepository = healthConditionRepository;
        this.ruleRepository = ruleRepository;
    }

    @Transactional(readOnly = true)
    public HealthAnalysisResponse analyze(
            HealthAnalysisRequest request) {

        validateRequest(request);

        Product product =
                productRepository.findById(request.getProductId())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Product not found with id: "
                                                + request.getProductId()));

        List<HealthAnalysisResponse.ConditionAnalysis>
                conditionAnalyses = new ArrayList<>();

        for (Long conditionId : request.getHealthConditionIds()) {

            HealthCondition condition =
                    healthConditionRepository.findById(conditionId)
                            .orElseThrow(() ->
                                    new RuntimeException(
                                            "Health condition not found with id: "
                                                    + conditionId));

            List<HealthConditionRule> rules =
                    ruleRepository.findByHealthConditionId(conditionId);

            List<HealthAnalysisResponse.HealthFinding> findings =
                    new ArrayList<>();

            for (HealthConditionRule rule : rules) {

                HealthAnalysisResponse.HealthFinding finding =
                        evaluateRule(product, rule);

                if (finding != null) {
                    findings.add(finding);
                }
            }

            String severity =
                    determineOverallSeverity(findings);

            String recommendation =
                    determineRecommendation(
                            severity,
                            findings
                    );

            conditionAnalyses.add(
                    new HealthAnalysisResponse.ConditionAnalysis(
                            condition.getId(),
                            condition.getName(),
                            severity,
                            recommendation,
                            findings
                    )
            );
        }

        String overallRecommendation =
                determineOverallRecommendation(
                        conditionAnalyses
                );

        return new HealthAnalysisResponse(
                product.getId(),
                product.getProductName(),
                conditionAnalyses,
                overallRecommendation
        );
    }

    private void validateRequest(
            HealthAnalysisRequest request) {

        if (request == null) {
            throw new IllegalArgumentException(
                    "Request body is required");
        }

        if (request.getProductId() == null) {
            throw new IllegalArgumentException(
                    "productId is required");
        }

        if (request.getHealthConditionIds() == null
                || request.getHealthConditionIds().isEmpty()) {

            throw new IllegalArgumentException(
                    "At least one health condition must be selected");
        }
    }

    private HealthAnalysisResponse.HealthFinding evaluateRule(
            Product product,
            HealthConditionRule rule) {

        String factor =
                normalize(rule.getFactor());

        Double productValue =
                getProductValue(product, factor);

        /*
         * Numeric rule.
         *
         * If a rule has a threshold and the product does not contain
         * the required nutrition value, we cannot evaluate it.
         */
        if (rule.getThresholdValue() != null) {

            if (productValue == null) {
                return null;
            }

            if (!evaluateNumericRule(
                    productValue,
                    rule.getOperator(),
                    rule.getThresholdValue())) {

                return null;
            }

            return new HealthAnalysisResponse.HealthFinding(
                    rule.getFactor(),
                    rule.getSeverity(),
                    productValue,
                    rule.getUnit(),
                    rule.getOperator(),
                    rule.getThresholdValue(),
                    rule.getRecommendation(),
                    rule.getReason(),
                    rule.getSource(),
                    rule.getSourceUrl()
            );
        }

        /*
         * Ingredient/presence rules.
         *
         * These rules do not have a numeric threshold.
         */
        if (isIngredientRule(factor)) {

            if (!ingredientRuleMatches(product, rule)) {
                return null;
            }

            return new HealthAnalysisResponse.HealthFinding(
                    rule.getFactor(),
                    rule.getSeverity(),
                    null,
                    rule.getUnit(),
                    rule.getOperator(),
                    null,
                    rule.getRecommendation(),
                    rule.getReason(),
                    rule.getSource(),
                    rule.getSourceUrl()
            );
        }

        /*
         * A rule without a threshold and without a supported
         * ingredient/presence evaluation cannot be evaluated safely.
         */
        return null;
    }

    private Double getProductValue(
            Product product,
            String factor) {

        return switch (factor) {

            case "energy", "energy100g" ->
                    product.getEnergy100g();

            case "fat", "fat100g" ->
                    product.getFat100g();

            case "saturatedfat", "saturatedfat100g" ->
                    product.getSaturatedFat100g();

            case "carbohydrates", "carbohydrates100g" ->
                    product.getCarbohydrates100g();

            case "sugars", "sugars100g" ->
                    product.getSugars100g();

            case "fiber", "fiber100g" ->
                    product.getFiber100g();

            case "proteins", "proteins100g", "protein" ->
                    product.getProteins100g();

            case "salt", "salt100g" ->
                    product.getSalt100g();

            default ->
                    null;
        };
    }

    private boolean evaluateNumericRule(
            Double productValue,
            String operator,
            Double threshold) {

        if (operator == null) {
            return false;
        }

        return switch (
                operator.trim().toUpperCase(Locale.ROOT)) {

            case ">" ->
                    productValue > threshold;

            case ">=" ->
                    productValue >= threshold;

            case "<" ->
                    productValue < threshold;

            case "<=" ->
                    productValue <= threshold;

            case "=",
                 "==",
                 "EQUALS" ->
                    Double.compare(
                            productValue,
                            threshold
                    ) == 0;

            default ->
                    false;
        };
    }

    private boolean isIngredientRule(
            String factor) {

        return factor.contains("ingredient")
                || factor.contains("presence")
                || factor.contains("gluten")
                || factor.contains("lactose");
    }

    private boolean ingredientRuleMatches(
            Product product,
            HealthConditionRule rule) {

        String ingredients =
                product.getIngredientsText();

        if (ingredients == null
                || ingredients.isBlank()) {

            return false;
        }

        String normalizedIngredients =
                normalize(ingredients);

        String factor =
                normalize(rule.getFactor());

        /*
         * Celiac/gluten rule.
         */
        if (factor.contains("gluten")) {

            return containsAny(
                    normalizedIngredients,
                    "wheat",
                    "barley",
                    "rye",
                    "gluten",
                    "malt"
            );
        }

        /*
         * Lactose rule.
         */
        if (factor.contains("lactose")) {

            return containsAny(
                    normalizedIngredients,
                    "lactose",
                    "milk",
                    "milk powder",
                    "skimmed milk",
                    "whey",
                    "whey powder",
                    "milk solids"
            );
        }

        return false;
    }

    private boolean containsAny(
            String text,
            String... values) {

        for (String value : values) {

            if (text.contains(
                    normalize(value))) {

                return true;
            }
        }

        return false;
    }

    private String determineOverallSeverity(
            List<HealthAnalysisResponse.HealthFinding> findings) {

        if (findings.isEmpty()) {
            return "NO_CONCERN_IDENTIFIED";
        }

        boolean hasHigh =
                findings.stream()
                        .anyMatch(f ->
                                "HIGH".equalsIgnoreCase(f.getSeverity())
                                        || "HIGH_CONCERN".equalsIgnoreCase(f.getSeverity()));

        if (hasHigh) {
            return "HIGH";
        }

        boolean hasModerate =
                findings.stream()
                        .anyMatch(f ->
                                "MODERATE".equalsIgnoreCase(f.getSeverity())
                                        || "MODERATE_CONCERN".equalsIgnoreCase(f.getSeverity()));

        if (hasModerate) {
            return "MODERATE";
        }

        boolean hasLow =
                findings.stream()
                        .anyMatch(f ->
                                "LOW".equalsIgnoreCase(f.getSeverity())
                                        || "LOW_CONCERN".equalsIgnoreCase(f.getSeverity()));

        if (hasLow) {
            return "LOW";
        }

        return "INFORMATION";
    }

    private String determineRecommendation(
            String severity,
            List<HealthAnalysisResponse.HealthFinding>
                    findings) {

        if ("HIGH".equals(severity)) {
            return "AVOID_OR_SEEK_MEDICAL_GUIDANCE";
        }

        if ("MODERATE".equals(severity)) {
            return "LIMIT_OR_REVIEW";
        }

        if ("LOW".equals(severity)) {
            return "CONSUME_WITH_CAUTION";
        }

        if ("INFORMATION".equals(severity)) {
            return "REVIEW";
        }

        return "NO_SPECIFIC_CONCERN_IDENTIFIED";
    }

    private String determineOverallRecommendation(
            List<HealthAnalysisResponse.ConditionAnalysis>
                    conditions) {

        if (conditions.isEmpty()) {
            return "NO_HEALTH_CONDITIONS_SELECTED";
        }

        boolean hasHigh =
                conditions.stream()
                        .anyMatch(condition ->
                                "HIGH".equalsIgnoreCase(
                                        condition.getOverallSeverity()));

        if (hasHigh) {
            return "AVOID_OR_SEEK_MEDICAL_GUIDANCE";
        }

        boolean hasModerate =
                conditions.stream()
                        .anyMatch(condition ->
                                "MODERATE".equalsIgnoreCase(
                                        condition.getOverallSeverity()));

        if (hasModerate) {
            return "LIMIT_OR_REVIEW";
        }

        boolean hasLow =
                conditions.stream()
                        .anyMatch(condition ->
                                "LOW".equalsIgnoreCase(
                                        condition.getOverallSeverity()));

        if (hasLow) {
            return "CONSUME_WITH_CAUTION";
        }

        return "NO_SPECIFIC_CONCERN_IDENTIFIED";
    }

    private String normalize(
            String value) {

        if (value == null) {
            return "";
        }

        return value
                .toLowerCase(Locale.ROOT)
                .replaceAll("[\\[\\](){}]", " ")
                .replaceAll("[,:;]", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }
}