package com.example.nlq.service;

import com.example.nlq.OnboardingStep;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
public class WorkflowApiClient {

    private final RestClient restClient;


    public WorkflowApiClient(
//            @Value("${workflow.api.base-url:http://localhost:8080}")
            @Value("${workflow.api.base-url}")
            String baseUrl) {

        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }


    public WorkflowApiResult executeStep(
            OnboardingStep step,
            String referenceId) {

        if (step == null) {
            throw new IllegalArgumentException(
                    "Onboarding step is required.");
        }

        if (referenceId == null || referenceId.isBlank()) {
            throw new IllegalArgumentException(
                    "referenceId is required.");
        }

        String endpoint = switch (step) {
            case OTP_VERIFICATION ->
                    "/workflows/otp/verify";
            case PROFILE_DETAILS ->
                    "/workflows/submit-profile";
            case ACCOUNT_STATEMENT ->
                    "/workflows/enter-account-statement";
            case UDYAM_DOCUMENT ->
                    "/workflows/udyam-document";
        };

        return restClient.post()
                .uri(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("referenceId", referenceId))
                .exchange((request, response) -> {
                    int status = response.getStatusCode().value();

                    String body;
                    try {
                        body = StreamUtils.copyToString(
                                response.getBody(),
                                StandardCharsets.UTF_8
                        );
                    } catch (IOException e) {
                        throw new IllegalStateException(
                                "Could not read workflow API response.",
                                e
                        );
                    }

                    return new WorkflowApiResult(
                            status,
                            response.getStatusCode().is2xxSuccessful(),
                            body
                    );
                });
    }

    public record WorkflowApiResult(
            int httpStatus,
            boolean success,
            String responseBody
    ) {}
}

