package com.packagedfood.recommendation.service;

import com.packagedfood.recommendation.dto.ProductAdditiveDetectionResponse;
import com.packagedfood.recommendation.entity.Additive;
import com.packagedfood.recommendation.entity.Product;
import com.packagedfood.recommendation.entity.ProductAdditive;
import com.packagedfood.recommendation.repository.AdditiveRepository;
import com.packagedfood.recommendation.repository.ProductAdditiveRepository;
import com.packagedfood.recommendation.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

@Service
public class ProductAdditiveDetectionService {

    private final ProductRepository productRepository;
    private final AdditiveRepository additiveRepository;
    private final ProductAdditiveRepository productAdditiveRepository;

    public ProductAdditiveDetectionService(
            ProductRepository productRepository,
            AdditiveRepository additiveRepository,
            ProductAdditiveRepository productAdditiveRepository) {

        this.productRepository = productRepository;
        this.additiveRepository = additiveRepository;
        this.productAdditiveRepository = productAdditiveRepository;
    }

    @Transactional
    public ProductAdditiveDetectionResponse detectAdditives(Long productId) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Product not found with id: " + productId));

        String ingredientsText = product.getIngredientsText();

        /*
         * If the product has no ingredient information,
         * remove any previous detection results and return empty result.
         */
        if (ingredientsText == null || ingredientsText.isBlank()) {

            productAdditiveRepository.deleteByProductId(productId);

            /*
             * Important:
             * Force DELETE to be executed in the database
             * before any new INSERT operations.
             */
            productAdditiveRepository.flush();

            return new ProductAdditiveDetectionResponse(
                    product.getId(),
                    product.getProductName(),
                    ingredientsText,
                    0,
                    List.of()
            );
        }

        String normalizedIngredients =
                normalizeText(ingredientsText);

        List<Additive> additives =
                additiveRepository.findAll();

        Map<Long, Additive> detectedAdditives =
                new LinkedHashMap<>();

        /*
         * Check every additive in the knowledge base
         * against the product ingredient text.
         */
        for (Additive additive : additives) {

            if (matchesAdditive(
                    normalizedIngredients,
                    additive)) {

                detectedAdditives.put(
                        additive.getId(),
                        additive
                );
            }
        }

        /*
         * Remove previous analysis results.
         */
        productAdditiveRepository.deleteByProductId(productId);

        /*
         * IMPORTANT FIX:
         *
         * Make sure the DELETE is executed in MySQL
         * before inserting the new detection results.
         *
         * Without this, running the same analysis twice
         * can cause:
         *
         * Duplicate entry 'productId-additiveId'
         */
        productAdditiveRepository.flush();

        /*
         * Create fresh detection records.
         *
         * detectedAmount and detectedUnit remain null
         * because ingredient text normally does not provide
         * the actual quantity of an additive.
         */
        List<ProductAdditive> productAdditives =
                detectedAdditives.values()
                        .stream()
                        .map(additive ->
                                new ProductAdditive(
                                        product,
                                        additive,
                                        null,
                                        null,
                                        determineDetectionMethod(
                                                normalizedIngredients,
                                                additive
                                        )
                                )
                        )
                        .toList();

        /*
         * Save the current detection results.
         */
        productAdditiveRepository.saveAll(productAdditives);

        /*
         * Flush the INSERT operations as well.
         */
        productAdditiveRepository.flush();

        return buildResponse(
                product,
                productAdditives
        );
    }

    @Transactional(readOnly = true)
    public ProductAdditiveDetectionResponse getDetectedAdditives(
            Long productId) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Product not found with id: " + productId));

        List<ProductAdditive> productAdditives =
                productAdditiveRepository.findByProductId(productId);

        return buildResponse(
                product,
                productAdditives
        );
    }

    private boolean matchesAdditive(
            String normalizedIngredients,
            Additive additive) {

        /*
         * Check E-number first.
         * Example:
         * E211
         * e211
         * E 211
         */
        if (matchesCode(
                normalizedIngredients,
                additive.getCode())) {

            return true;
        }

        /*
         * Check INS code.
         * Example:
         * INS 211
         */
        if (matchesInsCode(
                normalizedIngredients,
                additive.getCode())) {

            return true;
        }

        /*
         * Finally check the additive's full name.
         * Example:
         * sodium benzoate
         */
        return matchesName(
                normalizedIngredients,
                additive.getName()
        );
    }

    private boolean matchesCode(
            String text,
            String code) {

        if (code == null || code.isBlank()) {
            return false;
        }

        String numericPart =
                code.replaceAll("[^0-9]", "");

        if (numericPart.isBlank()) {
            return false;
        }

        String pattern =
                "\\be\\s*"
                        + Pattern.quote(numericPart)
                        + "\\b";

        return Pattern.compile(
                        pattern,
                        Pattern.CASE_INSENSITIVE
                )
                .matcher(text)
                .find();
    }

    private boolean matchesInsCode(
            String text,
            String code) {

        if (code == null || code.isBlank()) {
            return false;
        }

        String numericPart =
                code.replaceAll("[^0-9]", "");

        if (numericPart.isBlank()) {
            return false;
        }

        String pattern =
                "\\bins\\s*"
                        + Pattern.quote(numericPart)
                        + "\\b";

        return Pattern.compile(
                        pattern,
                        Pattern.CASE_INSENSITIVE
                )
                .matcher(text)
                .find();
    }

    private boolean matchesName(
            String text,
            String additiveName) {

        if (additiveName == null || additiveName.isBlank()) {
            return false;
        }

        String normalizedName =
                normalizeText(additiveName);

        String pattern =
                "(?<![a-z0-9])"
                        + Pattern.quote(normalizedName)
                        + "(?![a-z0-9])";

        return Pattern.compile(
                        pattern,
                        Pattern.CASE_INSENSITIVE
                )
                .matcher(text)
                .find();
    }

    private String normalizeText(String text) {

        if (text == null) {
            return "";
        }

        return text
                .toLowerCase(Locale.ROOT)
                .replaceAll("[\\[\\](){}]", " ")
                .replaceAll("[,:;]", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }

    private String determineDetectionMethod(
            String normalizedIngredients,
            Additive additive) {

        if (matchesCode(
                normalizedIngredients,
                additive.getCode())) {

            return "E_NUMBER";
        }

        if (matchesInsCode(
                normalizedIngredients,
                additive.getCode())) {

            return "INS_CODE";
        }

        return "NAME";
    }

    private ProductAdditiveDetectionResponse buildResponse(
            Product product,
            List<ProductAdditive> productAdditives) {

        List<ProductAdditiveDetectionResponse.DetectedAdditive>
                detectedAdditives =
                productAdditives.stream()
                        .map(this::convertToResponse)
                        .toList();

        return new ProductAdditiveDetectionResponse(
                product.getId(),
                product.getProductName(),
                product.getIngredientsText(),
                detectedAdditives.size(),
                detectedAdditives
        );
    }

    private ProductAdditiveDetectionResponse.DetectedAdditive
    convertToResponse(ProductAdditive productAdditive) {

        Additive additive =
                productAdditive.getAdditive();

        return new ProductAdditiveDetectionResponse.DetectedAdditive(
                additive.getId(),
                additive.getCode(),
                additive.getName(),
                additive.getCategory(),
                productAdditive.getDetectionMethod(),
                productAdditive.getDetectedAmount(),
                productAdditive.getDetectedUnit(),
                additive.getThresholdType(),
                additive.getThresholdValue(),
                additive.getThresholdUnit(),
                additive.getFoodCategory(),
                additive.getJurisdiction(),
                additive.getSource(),
                additive.getSourceUrl()
        );
    }
}