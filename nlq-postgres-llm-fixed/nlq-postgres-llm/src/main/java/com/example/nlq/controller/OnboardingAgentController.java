package com.example.nlq.controller;

import com.example.nlq.OnboardingStep;
import com.example.nlq.dto.OnboardingChatRequest;
import com.example.nlq.service.OnboardingPromptService;
import com.example.nlq.service.OnboardingStepRouter;
import com.example.nlq.service.WorkflowApiClient;
import io.agentscope.core.ReActAgent;
import io.agentscope.core.model.Model;
import io.agentscope.core.permission.PermissionBehavior;
import io.agentscope.core.permission.PermissionContextState;
import io.agentscope.core.permission.PermissionMode;
import io.agentscope.core.permission.PermissionRule;
import io.agentscope.core.tool.Toolkit;
import io.agentscope.core.tool.mcp.McpClientWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/agent/onboarding")
public class OnboardingAgentController {

    private final Model model;
    private final OnboardingPromptService promptService;
    private final OnboardingStepRouter stepRouter;
    private final WorkflowApiClient workflowApiClient;
    private final McpClientWrapper brainWorkflowMcpClient;
    private static final Logger log =
            LoggerFactory.getLogger(OnboardingAgentController.class);

    public OnboardingAgentController(
            Model model,
            OnboardingPromptService promptService,
            OnboardingStepRouter stepRouter,
            WorkflowApiClient workflowApiClient, McpClientWrapper brainWorkflowMcpClient) {

        this.model = model;
        this.promptService = promptService;
        this.stepRouter = stepRouter;
        this.workflowApiClient = workflowApiClient;
        this.brainWorkflowMcpClient = brainWorkflowMcpClient;
    }

//    @PostMapping("/chat")
//    public ResponseEntity<?> chat(
//            @RequestBody OnboardingChatRequest request) {
//
//        String question = request.question();
//
//        if (question == null || question.isBlank()) {
//            return ResponseEntity.badRequest().body(
//                    Map.of("error", "question is required.")
//            );
//        }
//
//        // 1. Identify which onboarding step the user is asking about.
//        OnboardingStepRouter.RoutingResult routing;
//
//        try {
//            routing = stepRouter.route(question);
//        } catch (RuntimeException e) {
//            return ResponseEntity.internalServerError().body(
//                    Map.of(
//                            "error",
//                            "Could not identify the requested onboarding step."
//                    )
//            );
//        }
//
//        // Current status requires a workflow-state lookup API.
//        if (routing.route()
//                == OnboardingStepRouter.Route.CURRENT_STATUS) {
//            return ResponseEntity.status(501).body(
//                    Map.of(
//                            "status", "STATUS_LOOKUP_NOT_CONFIGURED",
//                            "message",
//                            "Current workflow status lookup is not yet integrated."
//                    )
//            );
//        }
//
//        // 2. Resolve the selected step to a fixed enum value.
//        OnboardingStep selectedStep;
//
//        try {
//            selectedStep = OnboardingStep.fromStepNumber(
//                    routing.requestedStep()
//            );
//        } catch (IllegalArgumentException e) {
//            return ResponseEntity.badRequest().body(
//                    Map.of("error", e.getMessage())
//            );
//        }
//
//        // 3. Read the selected SOP and referenceId from lakeFS.
//        OnboardingPromptService.LoadedPrompt loadedPrompt;
//
//        try {
//            loadedPrompt =
//                    promptService.loadPromptDetails(selectedStep);
//        } catch (RuntimeException e) {
//            return ResponseEntity.internalServerError().body(
//                    Map.of(
//                            "error",
//                            "Could not load the onboarding SOP from lakeFS."
//                    )
//            );
//        }
//
//        // 4. Call only the fixed endpoint mapped to this step.
//        WorkflowApiClient.WorkflowApiResult workflowResult;
//
//        try {
//            workflowResult = workflowApiClient.executeStep(
//                    selectedStep,
//                    loadedPrompt.referenceId()
//            );
//        } catch (RestClientException e) {
//            return ResponseEntity.status(502).body(
//                    Map.of(
//                            "status", "WORKFLOW_API_UNAVAILABLE",
//                            "message",
//                            "Could not reach the workflow service."
//                    )
//            );
//        }
//
//        // 5. Preserve workflow failures; do not turn them into success.
//        if (!workflowResult.success()) {
//            return ResponseEntity
//                    .status(workflowResult.httpStatus())
//                    .contentType(MediaType.APPLICATION_JSON)
//                    .body(workflowResult.responseBody());
//        }
//
//        // 6. Only after confirmed success, ask Ollama to explain
//        //    the actual workflow response using the selected SOP.
//        String agentInput = """
//                User question:
//                %s
//
//                Selected onboarding step:
//                %s
//
//                Confirmed successful workflow API response:
//                %s
//
//                Explain the actual result to the user using the SOP.
//                Do not invent additional state changes or completed actions.
//                """.formatted(
//                question,
//                selectedStep.getDisplayName(),
//                workflowResult.responseBody()
//        );
//
//        var response = ReActAgent.builder()
//                .name("OnboardingAgent")
//                .sysPrompt(loadedPrompt.content())
//                .model(model)
//                .toolkit(new Toolkit())
//                .build()
//                .call(agentInput)
//                .block();
//
//        if (response == null
//                || response.getTextContent() == null
//                || response.getTextContent().isBlank()) {
//            return ResponseEntity.internalServerError().body(
//                    Map.of(
//                            "error",
//                            "Workflow succeeded, but the agent could not generate a response."
//                    )
//            );
//        }
//
//        return ResponseEntity.ok(
//                Map.of("answer", response.getTextContent())
//        );
//    }

    @PostMapping("/chat")
    public ResponseEntity<?> chat(
            @RequestBody OnboardingChatRequest request) {

        String question = request.question();

        if (question == null || question.isBlank()) {
            return ResponseEntity.badRequest().body(
                    Map.of("error", "question is required.")
            );
        }

        // 1. Identify the onboarding step from the user's question.
        OnboardingStepRouter.RoutingResult routing;

        try {
            routing = stepRouter.route(question);
        } catch (RuntimeException e) {
            return ResponseEntity.internalServerError().body(
                    Map.of("error", "Could not identify the onboarding step.")
            );
        }

        if (routing.route()
                == OnboardingStepRouter.Route.CURRENT_STATUS) {
            return ResponseEntity.status(501).body(
                    Map.of(
                            "status", "STATUS_LOOKUP_NOT_CONFIGURED",
                            "message",
                            "Current-state lookup is not yet integrated."
                    )
            );
        }

        OnboardingStep selectedStep =
                OnboardingStep.fromStepNumber(routing.requestedStep());

        // 2. Load the selected SOP and referenceId from lakeFS.
        OnboardingPromptService.LoadedPrompt loadedPrompt;

        try {
            loadedPrompt = promptService.loadPromptDetails(selectedStep);
        } catch (RuntimeException e) {
            return ResponseEntity.internalServerError().body(
                    Map.of("error", "Could not load the SOP from lakeFS.")
            );
        }

        // 3. Register MCP tools discovered from mcp-block-service.
        Toolkit toolkit = new Toolkit();

        try {
            toolkit.registerMcpClient(brainWorkflowMcpClient).block();
        } catch (RuntimeException e) {
            return ResponseEntity.status(502).body(
                    Map.of(
                            "status", "MCP_SERVICE_UNAVAILABLE",
                            "message", "Could not register MCP workflow tools."
                    )
            );
        }


        String toolInstruction = switch (selectedStep) {
            case OTP_VERIFICATION ->
                    "Call the registered tool named exactly verify_otp.";

            case PROFILE_DETAILS ->
                    "Call the registered tool named exactly submit_profile.";

            case ACCOUNT_STATEMENT ->
                    "Call the registered tool named exactly submit_account_statement.";

            case UDYAM_DOCUMENT ->
                    "Call the registered tool named exactly submit_udyam_document.";
        };



//        String toolInstruction = switch (selectedStep) {
//            case OTP_VERIFICATION ->
//                    "Call the MCP tool mcp__brain-workflow-tools__verify_otp.";
//            case PROFILE_DETAILS ->
//                    "Call the MCP tool mcp__brain-workflow-tools__submit_profile.";
//            case ACCOUNT_STATEMENT ->
//                    "Call the MCP tool mcp__brain-workflow-tools__submit_account_statement.";
//            case UDYAM_DOCUMENT ->
//                    "Call the MCP tool mcp__brain-workflow-tools__submit_udyam_document.";
//        };

//        String toolInstruction = switch (selectedStep) {
//            case OTP_VERIFICATION ->
//                    "For an explicit request to perform OTP verification, "
//                            + "use the registered verify_otp MCP tool.";
//            case PROFILE_DETAILS ->
//                    "For an explicit request to submit profile details, "
//                            + "use the registered submit_profile MCP tool.";
//            case ACCOUNT_STATEMENT ->
//                    "For an explicit request to submit an account statement, "
//                            + "use the registered submit_account_statement MCP tool.";
//            case UDYAM_DOCUMENT ->
//                    "For an explicit request to submit the Udyam document, "
//                            + "use the registered submit_udyam_document MCP tool.";
//        };

//        String agentInput = """
//            User question:
//            %s
//
//            Selected onboarding step:
//            %s
//
//            Reference ID from the selected lakeFS SOP:
//            %s
//
//            Instructions:
//            %s
//
//            Use only the corresponding registered MCP tool when the user
//            explicitly asks to perform the workflow action.
//            Pass the reference ID specified above to that tool.
//            For informational questions, explain the SOP without executing
//            a workflow action.
//            Never claim an action succeeded unless the tool result confirms
//            success. If the tool reports a workflow error, explain the actual
//            error and do not claim success.
//            """.formatted(
//                question,
//                selectedStep.getDisplayName(),
//                loadedPrompt.referenceId(),
//                toolInstruction
//        );

        String agentInput = """
        USER QUESTION:
        %s

        SELECTED ONBOARDING STEP:
        %s

        REFERENCE ID FROM LAKEFS SOP:
        %s

        REQUIRED MCP TOOL:
        %s

        INSTRUCTIONS:
        1. Use only the specified MCP tool for this workflow action.
        2. Pass the referenceId provided above to the tool.
        3. Wait for the tool execution result.
        4. Read the complete tool result before answering.
        5. If the workflow succeeds, explain the confirmed result
           in simple, user-friendly language.
        6. If the workflow returns an error, explain the actual
           failure and do not claim that the action succeeded.
        7. Never invent workflow state, API results, or completed steps.
        8. After the tool returns, always provide a non-empty,
           user-friendly final answer.
        9. Do not return an empty response or merely announce
           that you are calling a tool.
        """.formatted(
                question,
                selectedStep.getDisplayName(),
                loadedPrompt.referenceId(),
                toolInstruction
        );

        // 4. ReActAgent invokes the MCP tool; Ollama generates the response.
        var response = ReActAgent.builder()
                .name("OnboardingAgent")
                .sysPrompt(loadedPrompt.content())
                .model(model)
                .toolkit(toolkit)
                .permissionContext(permissionContextFor(selectedStep))
                .build()
                .call(agentInput)
                .block();

//        var response = ReActAgent.builder()
//                .name("OnboardingAgent")
//                .sysPrompt(loadedPrompt.content())
//                .model(model)
//                .toolkit(toolkit)
//                .build()
//                .call(agentInput)
//                .block();

        String responseText =
                response == null ? null : response.getTextContent();

        log.info("Onboarding agent response is null: {}", response == null);
        log.info("Onboarding agent text is null: {}", responseText == null);
        log.info("Onboarding agent text length: {}",
                responseText == null ? -1 : responseText.length());

        log.debug("Onboarding agent raw response: {}", response);

        log.info("Agent generate reason: {}",
                response == null ? null : response.getGenerateReason());

        log.info("Agent response role: {}",
                response == null ? null : response.getRole());

        log.info("Agent response content blocks: {}",
                response == null
                        ? "null"
                        : response.getContent().stream()
                        .map(block -> block.getClass().getSimpleName())
                        .toList());

        log.info("Agent raw response: {}", response);

        if (response == null
                || response.getTextContent() == null
                || response.getTextContent().isBlank()) {
            return ResponseEntity.internalServerError().body(
                    Map.of("error", "Onboarding agent returned an empty response.")
            );
        }

        return ResponseEntity.ok(
                Map.of("answer", response.getTextContent())
        );
    }


    private PermissionContextState permissionContextFor(
            OnboardingStep step) {

        String toolName = switch (step) {
            case OTP_VERIFICATION -> "verify_otp";
            case PROFILE_DETAILS -> "submit_profile";
            case ACCOUNT_STATEMENT -> "submit_account_statement";
            case UDYAM_DOCUMENT -> "submit_udyam_document";
        };

        return PermissionContextState.builder()
                .mode(PermissionMode.DEFAULT)
                .addAllowRule(
                        toolName,
                        new PermissionRule(
                                toolName,
                                null,
                                PermissionBehavior.ALLOW,
                                "onboarding-user-request"
                        )
                )
                .build();
    }


}

//
//import com.example.nlq.OnboardingStep;
//import com.example.nlq.dto.OnboardingChatRequest;
//import com.example.nlq.service.OnboardingPromptService;
//import com.example.nlq.service.OnboardingStepRouter;
//import com.example.nlq.service.WorkflowApiClient;
//import io.agentscope.core.ReActAgent;
//import io.agentscope.core.model.Model;
//import io.agentscope.core.tool.Toolkit;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.Map;
//
//@RestController
//@RequestMapping("/api/v1/agent/onboarding")
//public class OnboardingAgentController {
//
//    private final Model model;
//    private final OnboardingPromptService promptService;
//    private final OnboardingStepRouter stepRouter;
//    private final WorkflowApiClient workflowApiClient;
//
//    public OnboardingAgentController(
//            Model model,
//            OnboardingPromptService promptService, OnboardingStepRouter stepRouter, WorkflowApiClient workflowApiClient) {
//        this.model = model;
//        this.promptService = promptService;
//        this.stepRouter = stepRouter;
//        this.workflowApiClient = workflowApiClient;
//    }

//    @PostMapping("/chat")
//    public Map<String, String> chat(
//            @RequestBody OnboardingChatRequest request) {
//
//        if (request.message() == null
//                || request.message().isBlank()) {
//            throw new IllegalArgumentException(
//                    "message is required.");
//        }
//
//        if (request.currentStep() == null
//                || request.currentStepName() == null
//                || request.brainDecision() == null
//                || request.brainDecision().isBlank()) {
//            throw new IllegalArgumentException(
//                    "Brain context is incomplete.");
//        }
//
//        String completedSteps =
//                request.completedSteps() == null
//                        ? "Not provided"
//                        : String.join(", ", request.completedSteps());
//
//        String requestedStep =
//                request.requestedStep() == null
//                        ? "None specified"
//                        : request.requestedStep().toString();
//
//        String decisionReason =
//                request.decisionReason() == null
//                        ? "Not provided"
//                        : request.decisionReason();
//
//        String agentInput = """
//                BRAIN CONTEXT — authoritative information:
//                Current step number: %s
//                Current step name: %s
//                Completed steps: %s
//                Requested step number: %s
//                Brain decision: %s
//                Brain decision reason: %s
//
//                User message:
//                %s
//
//                Explain the supplied status or decision to the user.
//                Do not independently make workflow decisions.
//                """.formatted(
//                request.currentStep(),
//                request.currentStepName(),
//                completedSteps,
//                requestedStep,
//                request.brainDecision(),
//                decisionReason,
//                request.message()
//        );
//
//
////        int promptStepNumber = request.requestedStep() != null
////                ? request.requestedStep()
////                : request.currentStep();
////
////        OnboardingStep selectedPromptStep =
////                OnboardingStep.fromStepNumber(promptStepNumber);
//
//        OnboardingStepRouter.RoutingResult routing =
//                stepRouter.route(request.message());
//
//        int promptStepNumber;
//
//        if (routing.route()
//                == OnboardingStepRouter.Route.CURRENT_STATUS) {
//
//            // Current-step question: use the step supplied by Brain.
//            promptStepNumber = request.currentStep();
//
//        } else {
//            // Step-specific question: use the detected intent.
//            promptStepNumber = routing.requestedStep();
//        }
//
//        OnboardingStep selectedPromptStep =
//                OnboardingStep.fromStepNumber(promptStepNumber);
//
//        ReActAgent agent = ReActAgent.builder()
//                .name("OnboardingAgent")
//                .sysPrompt(promptService.loadPrompt(selectedPromptStep))
//                .model(model)
//                .toolkit(new Toolkit())
//                .build();
////        ReActAgent agent = ReActAgent.builder()
////                .name("OnboardingAgent")
////                .sysPrompt(promptService.loadPrompt())
////                .model(model)
////                .toolkit(new Toolkit())
////                .build();
//
//        var response = agent.call(agentInput).block();
//
//        if (response == null
//                || response.getTextContent() == null
//                || response.getTextContent().isBlank()) {
//            throw new IllegalStateException(
//                    "Onboarding agent returned an empty response.");
//        }
//
//        return Map.of(
//                "answer",
//                response.getTextContent()
//        );
//    }


//@PostMapping("/chat")
//public ResponseEntity<?> chat(
//        @RequestBody OnboardingChatRequest request) {
//
//    if (request.message() == null || request.message().isBlank()) {
//        return ResponseEntity.badRequest().body(
//                Map.of("error", "message is required.")
//        );
//    }
//
//    if (request.currentStep() == null
//            || request.currentStepName() == null
//            || request.brainDecision() == null
//            || request.brainDecision().isBlank()) {
//        return ResponseEntity.badRequest().body(
//                Map.of("error", "Current workflow context is required.")
//        );
//    }
//
//    OnboardingStepRouter.RoutingResult routing =
//            stepRouter.route(request.message());
//
//    boolean isStatusQuestion =
//            routing.route()
//                    == OnboardingStepRouter.Route.CURRENT_STATUS;
//
//    int selectedStepNumber = isStatusQuestion
//            ? request.currentStep()
//            : routing.requestedStep();
//
//    OnboardingStep selectedStep;
//
//    try {
//        selectedStep =
//                OnboardingStep.fromStepNumber(selectedStepNumber);
//    } catch (IllegalArgumentException e) {
//        return ResponseEntity.badRequest().body(
//                Map.of("error", e.getMessage())
//        );
//    }
//
//    // Load the SOP for the selected step.
////    String sop = promptService.loadPrompt(selectedStep);
//    OnboardingPromptService.LoadedPrompt loadedPrompt =
//            promptService.loadPromptDetails(selectedStep);
//
//    String sop = loadedPrompt.content();
//    // Status questions only explain the context received from Brain.
//    if (isStatusQuestion) {
//        return generateUserResponse(
//                sop,
//                request,
//                "The user is asking about the current workflow status. "
//                        + "Explain the supplied current step and completed steps. "
//                        + "Do not claim to have checked Brain independently."
//        );
//    }
//
//    // Do not call a workflow API when the supplied decision blocks it.
//    if (!"ALLOW".equalsIgnoreCase(request.brainDecision().trim())) {
//        return ResponseEntity.status(409).body(
//                Map.of(
//                        "status", "BLOCKED",
//                        "currentStep", request.currentStep(),
//                        "currentStepName", request.currentStepName(),
//                        "requestedStep", selectedStepNumber,
//                        "reason", request.decisionReason() == null
//                                ? "The requested workflow transition is not allowed."
//                                : request.decisionReason(),
//                        "workflowApiCalled", false
//                )
//        );
//    }
//
//    if (request.referenceId() == null
//            || request.referenceId().isBlank()) {
//        return ResponseEntity.badRequest().body(
//                Map.of("error", "referenceId is required.")
//        );
//    }
//
//    // Call only the fixed endpoint mapped to the selected step.
//    WorkflowApiClient.WorkflowApiResult workflowResult;
//
//    try {
////        workflowResult = workflowApiClient.executeStep(
////                selectedStep,
////                request.referenceId()
////        );
//        workflowResult = workflowApiClient.executeStep(
//                selectedStep,
//                loadedPrompt.referenceId()
//        );
//
//    } catch (org.springframework.web.client.RestClientException e) {
//        return ResponseEntity.status(502).body(
//                Map.of(
//                        "status", "WORKFLOW_API_UNAVAILABLE",
//                        "message", "Could not reach the workflow service."
//                )
//        );
//    }
//
//    // Preserve the workflow service's failure response.
//    if (!workflowResult.success()) {
//        return ResponseEntity
//                .status(workflowResult.httpStatus())
//                .contentType(MediaType.APPLICATION_JSON)
//                .body(workflowResult.responseBody());
//    }
//
//    // Only after confirmed workflow success, use Ollama for the reply.
//    String workflowContext = """
//            The requested workflow API completed successfully.
//
//            Reference ID: %s
//            Requested step: %s
//            Workflow HTTP status: %d
//            Actual workflow response body:
//            %s
//
//            Explain this confirmed result to the user in simple language.
//            Use the returned state when explaining what happens next.
//            Do not claim any additional action was completed.
//            """.formatted(
//            request.referenceId(),
//            selectedStep.getDisplayName(),
//            workflowResult.httpStatus(),
//            workflowResult.responseBody()
//    );
//
//    return generateUserResponse(sop, request, workflowContext);
//}
//
//    private ResponseEntity<?> generateUserResponse(
//            String sop,
//            OnboardingChatRequest request,
//            String workflowContext) {
//
//        String completedSteps = request.completedSteps() == null
//                ? "Not provided"
//                : String.join(", ", request.completedSteps());
//
//        String agentInput = """
//            USER MESSAGE:
//            %s
//
//            SUPPLIED WORKFLOW CONTEXT:
//            Current step: %d - %s
//            Completed steps: %s
//            Requested step: %s
//            Decision reason: %s
//
//            WORKFLOW INFORMATION:
//            %s
//
//            Answer the user according to the supplied SOP and confirmed
//            information. Do not invent workflow state or API results.
//            """.formatted(
//                request.message(),
//                request.currentStep(),
//                request.currentStepName(),
//                completedSteps,
//                request.requestedStep() == null
//                        ? "Not specified"
//                        : request.requestedStep(),
//                request.decisionReason() == null
//                        ? "Not provided"
//                        : request.decisionReason(),
//                workflowContext
//        );
//
//        ReActAgent agent = ReActAgent.builder()
//                .name("OnboardingAgent")
//                .sysPrompt(sop)
//                .model(model)
//                .toolkit(new Toolkit())
//                .build();
//
//        var response = agent.call(agentInput).block();
//
//        if (response == null
//                || response.getTextContent() == null
//                || response.getTextContent().isBlank()) {
//            throw new IllegalStateException(
//                    "Onboarding agent returned an empty response."
//            );
//        }
//
//        return ResponseEntity.ok(
//                Map.of("answer", response.getTextContent())
//        );
//    }
//
//}

