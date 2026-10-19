//
//package com.example.nlq.controller;
//
//import com.example.nlq.dto.QueryResponse;
//import com.example.nlq.security.SqlValidator;
//import com.example.nlq.service.AnswerGenerationServiceAS;
//import com.example.nlq.service.HitlApprovalService;
//import com.example.nlq.service.SqlExecutionService;
//import com.example.nlq.service.SqlGenerationServiceAS;
//import com.example.nlq.tool.Nl2SqlExecutionTools;
//import com.example.nlq.tool.Nl2SqlTools;
//import com.example.nlq.tool.Nl2SqlWorkflowTool;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import io.agentscope.core.ReActAgent;
//import io.agentscope.core.model.Model;
//import io.agentscope.core.tool.Toolkit;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.atomic.AtomicReference;
//
//@RestController
//@RequestMapping("/api/v1/agent")
//public class Nl2SqlAgentController {
//
//    private final Model model;
//    private final SqlGenerationServiceAS sqlGenerationServiceAS;
//    private final SqlValidator sqlValidator;
//    private final HitlApprovalService hitlApprovalService;
//    private final SqlExecutionService sqlExecutionService;
//    private final AnswerGenerationServiceAS answerGenerationServiceAS;
//    private final ObjectMapper objectMapper;
//
//    public Nl2SqlAgentController(
//            Model model,
//            SqlGenerationServiceAS sqlGenerationServiceAS,
//            SqlValidator sqlValidator,
//            HitlApprovalService hitlApprovalService,
//            SqlExecutionService sqlExecutionService,
//            AnswerGenerationServiceAS answerGenerationServiceAS,
//            ObjectMapper objectMapper) {
//
//        this.model = model;
//        this.sqlGenerationServiceAS = sqlGenerationServiceAS;
//        this.sqlValidator = sqlValidator;
//        this.hitlApprovalService = hitlApprovalService;
//        this.sqlExecutionService = sqlExecutionService;
//        this.answerGenerationServiceAS = answerGenerationServiceAS;
//        this.objectMapper = objectMapper;
//    }
//
//    private ReActAgent createAgent(Toolkit toolkit, String prompt) {
//        return ReActAgent.builder()
//                .name("Nl2SqlAgent")
//                .sysPrompt(prompt)
//                .model(model)
//                .toolkit(toolkit)
//                .build();
//    }
//
//    private Toolkit createPrepareToolkit(
//            java.util.function.Consumer<String> validatedSqlConsumer) {
//
//        Toolkit toolkit = new Toolkit();
//
//        toolkit.registerTool(new Nl2SqlTools(
//                sqlGenerationServiceAS,
//                sqlValidator,
//                validatedSqlConsumer
//        ));
//
//        return toolkit;
//    }
//
//    @GetMapping("/test")
//    public String testAgent(@RequestParam String question) {
//
//        Toolkit toolkit = createPrepareToolkit(sql -> {
//        });
//
//        var response = createAgent(
//                toolkit,
//                """
//                        You are an NL2SQL assistant.
//
//                        1. Call generate_sql with the user's question.
//                        2. Call validate_sql with the exact generated SQL.
//                        3. Return the validated SQL.
//
//                        Never execute SQL.
//                        Never claim that database results were retrieved.
//                        If validation fails, report the error.
//                        """
//        ).call(question).block();
//
//        if (response == null) {
//            throw new IllegalStateException(
//                    "AgentScope returned a null response."
//            );
//        }
//
//        return response.getTextContent();
//    }
//
//    @PostMapping("/prepare")
//    public Map<String, Object> prepare(
//            @RequestBody Map<String, String> request) {
//
//        String question = request.get("question");
//
//        if (question == null || question.isBlank()) {
//            throw new IllegalArgumentException("question is required.");
//        }
//
//        AtomicReference<String> validatedSql = new AtomicReference<>();
//
//        Toolkit toolkit = createPrepareToolkit(validatedSql::set);
//
//        var response = createAgent(
//                toolkit,
//                """
//                        You are an NL2SQL preparation assistant.
//
//                        1. Call generate_sql with the user's question.
//                        2. Call validate_sql with the exact SQL returned.
//                        3. Return the validated SQL.
//
//                        Never execute SQL.
//                        Never claim that database results were retrieved.
//                        If validation fails, report the error.
//                        """
//        ).call(question).block();
//
//        if (response == null) {
//            throw new IllegalStateException(
//                    "AgentScope returned a null response."
//            );
//        }
//
//        String sql = validatedSql.get();
//
//        if (sql == null || sql.isBlank()) {
//            throw new IllegalStateException(
//                    "Agent did not complete SQL validation. Details: "
//                            + response.getTextContent()
//            );
//        }
//
//        var pending = hitlApprovalService.createPendingRequest(
//                question, sql
//        );
//
//        return Map.of(
//                "requestId", pending.requestId(),
//                "question", pending.question(),
//                "sql", pending.sql(),
//                "status", pending.status().name()
//        );
//    }
//
//    @GetMapping("/request/{requestId}")
//    public Map<String, Object> getRequest(
//            @PathVariable String requestId) {
//
//        var request =
//                hitlApprovalService.getPendingRequest(requestId);
//
//        return Map.of(
//                "requestId", request.requestId(),
//                "question", request.question(),
//                "sql", request.sql(),
//                "status", request.status().name(),
//                "createdAt", request.createdAt().toString()
//        );
//    }
//
////    @PostMapping("/query")
////    public QueryResponse query(
////            @RequestBody Map<String, String> request) {
////
////        String question = request.get("question");
////
////        if (question == null || question.isBlank()) {
////            throw new IllegalArgumentException("question is required.");
////        }
////
////        AtomicReference<String> validatedSql = new AtomicReference<>();
////        AtomicReference<List<Map<String, Object>>> rows =
////                new AtomicReference<>();
////        AtomicReference<String> answer = new AtomicReference<>();
////
////        Toolkit toolkit = new Toolkit();
////
////        toolkit.registerTool(new Nl2SqlExecutionTools(
////                sqlGenerationServiceAS,
////                sqlValidator,
////                sqlExecutionService,
////                answerGenerationServiceAS,
////                objectMapper,
////                question,
////                validatedSql,
////                rows,
////                answer
////        ));
////
////
////        var response = createAgent(
////                toolkit,
////                """
////                You are an NL2SQL assistant using the available tools.
////
////                Follow these steps in order:
////                1. Call generate_sql with the user's exact question.
////                2. Pass the exact SQL returned by generate_sql to validate_sql.
////                3. Wait for validation to succeed.
////                4. Call execute_sql with no arguments.
////                5. Wait for the actual database rows returned by execute_sql.
////                6. Call generate_answer with no arguments.
////                7. Return the answer produced by generate_answer.
////
////                Rules:
////                - Never invent SQL validation results or database rows.
////                - Never claim execution succeeded unless execute_sql succeeds.
////                - Do not call execute_sql if validation fails.
////                - Do not pass arguments to execute_sql or generate_answer.
////                - If a tool fails, stop and report the failure.
////                - Base the final answer on the actual tool results.
////                """
////        ).call(question).block();
////
////
//
//    /// /        var response = createAgent(
//    /// /                toolkit,
//    /// /                """
//    /// /                You are an NL2SQL assistant using the available tools.
//    /// /
//    /// /                Follow these steps in order:
//    /// /                1. Call generate_sql with the user's exact question.
//    /// /                2. Pass the exact generated SQL to validate_sql.
//    /// /                3. Wait for validation to succeed.
//    /// /                4. Pass the exact validated SQL to execute_sql.
//    /// /                5. Wait for the database result.
//    /// /                6. Pass the exact validated SQL to generate_answer.
//    /// /                7. Return the answer produced by generate_answer.
//    /// /
//    /// /                Never invent database rows or validation results.
//    /// /                Never skip a tool step.
//    /// /                If a tool fails, stop and report the error.
//    /// /                """
//    /// /        ).call(question).block();
////
////        if (response == null) {
////            throw new IllegalStateException(
////                    "AgentScope returned a null response."
////            );
////        }
////
////        String sql = validatedSql.get();
////        List<Map<String, Object>> data = rows.get();
////        String finalAnswer = answer.get();
////
////        if (sql == null || data == null || finalAnswer == null
////                || finalAnswer.isBlank()) {
////            throw new IllegalStateException(
////                    "AgentScope did not complete all required steps. "
////                            + "Agent response: " + response.getTextContent()
////            );
////        }
////
////        return new QueryResponse(
////                question,
////                finalAnswer,
////                sql,
////                data
////        );
////    }
//    @PostMapping("/query")
//    public QueryResponse query(
//            @RequestBody Map<String, String> request) {
//
//        String question = request.get("question");
//
//        if (question == null || question.isBlank()) {
//            throw new IllegalArgumentException(
//                    "question is required."
//            );
//        }
//
//        AtomicReference<QueryResponse> queryResult =
//                new AtomicReference<>();
//
//        Toolkit toolkit = new Toolkit();
//
//        toolkit.registerTool(new Nl2SqlWorkflowTool(
//                sqlGenerationServiceAS,
//                sqlValidator,
//                sqlExecutionService,
//                answerGenerationServiceAS,
//                queryResult::set
//        ));
//
//        var response = createAgent(
//                toolkit,
//                """
//                        You are an NL2SQL assistant.
//
//                        Call run_nl2sql exactly once with the user's question.
//                        Wait for the tool to complete successfully.
//                        Do not invent SQL, database rows, or answers.
//                        If the tool fails, report the failure.
//                        Do not reproduce the database result yourself.
//                        """
//        ).call(question).block();
//
//        if (response == null) {
//            throw new IllegalStateException(
//                    "AgentScope returned a null response."
//            );
//        }
//
//        QueryResponse result = queryResult.get();
//
//        if (result == null) {
//            throw new IllegalStateException(
//                    "AgentScope did not complete the NL2SQL workflow. "
//                            + "Agent response: "
//                            + response.getTextContent()
//            );
//        }
//
//        return result;
//    }
//
//}
