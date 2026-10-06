package com.packagedfood.recommendation.client;

import com.packagedfood.recommendation.dto.AiPredictionRequest;
import com.packagedfood.recommendation.dto.AiPredictionResponse;
import com.packagedfood.recommendation.exception.AiServiceException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class AiPredictionClient {

    private final RestClient restClient;

    public AiPredictionClient(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder
                .baseUrl("http://localhost:5000")
                .build();
    }

    public AiPredictionResponse predict(
            AiPredictionRequest request) {

        try {

            AiPredictionResponse response = restClient.post()
                    .uri("/predict")
                    .body(request)
                    .retrieve()
                    .body(AiPredictionResponse.class);

            if (response == null) {
                throw new AiServiceException(
                        "AI service returned an empty response."
                );
            }

            return response;

        } catch (AiServiceException exception) {

            throw exception;

        } catch (Exception exception) {

            throw new AiServiceException(
                    "Unable to communicate with the AI service. " +
                            "Make sure the Flask AI service is running on port 5000.",
                    exception
            );
        }
    }
}