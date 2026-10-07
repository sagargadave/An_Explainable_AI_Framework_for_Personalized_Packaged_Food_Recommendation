package com.packagedfood.recommendation.client;

import com.packagedfood.recommendation.dto.OpenFoodFactsProductDTO;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Component
public class OpenFoodFactsClient {

    private final RestClient restClient;

    public OpenFoodFactsClient(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder
                .baseUrl("https://world.openfoodfacts.org")
                .build();
    }

    public List<OpenFoodFactsProductDTO> searchProducts(String productName) {

        Map response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/cgi/search.pl")
                        .queryParam("search_terms", productName)
                        .queryParam("search_simple", "1")
                        .queryParam("action", "process")
                        .queryParam("json", "1")
                        .queryParam("page_size", "10")
                        .queryParam(
                                "fields",
                                "code,product_name,brands,categories," +
                                        "ingredients_text,image_url," +
                                        "additives,additives_tags," +
                                        "serving_size,serving_quantity," +
                                        "energy_100g,fat_100g," +
                                        "saturated-fat_100g,carbohydrates_100g," +
                                        "sugars_100g,fiber_100g," +
                                        "proteins_100g,salt_100g"
                        )
                        .build())
                .retrieve()
                .body(Map.class);

        if (response == null || response.get("products") == null) {
            return List.of();
        }

        List<Map<String, Object>> products =
                (List<Map<String, Object>>) response.get("products");

        return products.stream()
                .map(this::convertToDTO)
                .toList();
    }

    private OpenFoodFactsProductDTO convertToDTO(
            Map<String, Object> product) {

        OpenFoodFactsProductDTO dto =
                new OpenFoodFactsProductDTO();

        /*
         * ---------------------------------------------------------
         * BASIC PRODUCT INFORMATION
         * ---------------------------------------------------------
         */

        dto.setCode(
                (String) product.get("code")
        );

        dto.setProduct_name(
                (String) product.get("product_name")
        );

        dto.setBrands(
                (String) product.get("brands")
        );

        dto.setCategories(
                (String) product.get("categories")
        );

        dto.setIngredients_text(
                (String) product.get("ingredients_text")
        );

        dto.setImage_url(
                (String) product.get("image_url")
        );

        /*
         * ---------------------------------------------------------
         * ADDITIVE INFORMATION
         * ---------------------------------------------------------
         */

        dto.setAdditives(
                (String) product.get("additives")
        );

        Object additivesTagsValue =
                product.get("additives_tags");

        if (additivesTagsValue instanceof List<?> list) {

            List<String> additivesTags =
                    list.stream()
                            .filter(String.class::isInstance)
                            .map(String.class::cast)
                            .toList();

            dto.setAdditives_tags(additivesTags);

        } else {

            dto.setAdditives_tags(List.of());
        }

        /*
         * ---------------------------------------------------------
         * SERVING INFORMATION
         * ---------------------------------------------------------
         *
         * IMPORTANT:
         * serving_quantity is the quantity of the complete
         * food serving. It is NOT the quantity of an additive.
         * ---------------------------------------------------------
         */

        dto.setServing_size(
                (String) product.get("serving_size")
        );

        dto.setServing_quantity(
                toDouble(product.get("serving_quantity"))
        );

        /*
         * ---------------------------------------------------------
         * NUTRITION INFORMATION
         * ---------------------------------------------------------
         */

        dto.setEnergy_100g(
                toDouble(product.get("energy_100g"))
        );

        dto.setFat_100g(
                toDouble(product.get("fat_100g"))
        );

        dto.setSaturated_fat_100g(
                toDouble(product.get("saturated-fat_100g"))
        );

        dto.setCarbohydrates_100g(
                toDouble(product.get("carbohydrates_100g"))
        );

        dto.setSugars_100g(
                toDouble(product.get("sugars_100g"))
        );

        dto.setFiber_100g(
                toDouble(product.get("fiber_100g"))
        );

        dto.setProteins_100g(
                toDouble(product.get("proteins_100g"))
        );

        dto.setSalt_100g(
                toDouble(product.get("salt_100g"))
        );

        return dto;
    }

    private Double toDouble(Object value) {

        if (value == null) {
            return null;
        }

        if (value instanceof Number number) {
            return number.doubleValue();
        }

        try {
            return Double.parseDouble(
                    value.toString()
            );

        } catch (NumberFormatException exception) {

            return null;
        }
    }
}