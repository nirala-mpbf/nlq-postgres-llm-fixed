package com.example.nlq.service;

import com.example.nlq.OnboardingStep;
import io.agentscope.core.ReActAgent;
import io.agentscope.core.model.Model;
import io.agentscope.core.tool.Toolkit;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class OnboardingStepRouter {

    private final Model model;

    private static final Pattern ROUTE_PATTERN =
            Pattern.compile(
                    "\\b(CURRENT_STATUS|STEP_[1-4])\\b",
                    Pattern.CASE_INSENSITIVE
            );

    public OnboardingStepRouter(Model model) {
        this.model = model;
    }

    public RoutingResult route(String userMessage) {

        if (userMessage == null || userMessage.isBlank()) {
            throw new IllegalArgumentException(
                    "User message is required."
            );
        }

        String routingPrompt = """
                You are an onboarding intent classifier.

                Classify the user's message into exactly ONE label.

                CURRENT_STATUS:
                The user asks which step they are currently on,
                what their current status is, or what remains pending.

                STEP_1:
                The user asks about OTP verification.

                STEP_2:
                The user asks about entering or submitting profile details.

                STEP_3:
                The user asks about an account statement or
                uploading an account statement.

                STEP_4:
                The user asks about a Udyam document or
                uploading the Udyam document.

                Return ONLY one label:
                CURRENT_STATUS, STEP_1, STEP_2, STEP_3, or STEP_4.

                Do not answer the user's question.
                Do not decide whether a workflow action is allowed.
                Do not invent the user's current workflow state.
                """;

        var response = ReActAgent.builder()
                .name("OnboardingStepRouter")
                .sysPrompt(routingPrompt)
                .model(model)
                .toolkit(new Toolkit())
                .build()
                .call(userMessage)
                .block();

        if (response == null
                || response.getTextContent() == null) {
            throw new IllegalStateException(
                    "Onboarding router returned an empty response."
            );
        }

        String output = response.getTextContent()
                .toUpperCase(Locale.ROOT);

        Matcher matcher = ROUTE_PATTERN.matcher(output);

        if (!matcher.find()) {
            throw new IllegalStateException(
                    "Could not identify onboarding intent. "
                            + "Router response: " + output
            );
        }

        Route route = Route.valueOf(matcher.group(1));

        int requestedStep = switch (route) {
            case STEP_1 -> 1;
            case STEP_2 -> 2;
            case STEP_3 -> 3;
            case STEP_4 -> 4;
            case CURRENT_STATUS -> 0;
        };

        return new RoutingResult(route, requestedStep);
    }

    public enum Route {
        CURRENT_STATUS,
        STEP_1,
        STEP_2,
        STEP_3,
        STEP_4
    }

    public record RoutingResult(
            Route route,
            int requestedStep
    ) {}
}

