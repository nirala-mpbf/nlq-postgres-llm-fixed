//package com.example.nlq.service;
//
//import com.example.nlq.config.PromptTemplates;
//import io.agentscope.core.message.TextBlock;
//import io.agentscope.core.message.UserMessage;
//import io.agentscope.core.model.ChatResponse;
//import io.agentscope.core.model.Model;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//import java.util.Objects;
//import java.util.stream.Collectors;
//
//@Service
//public class SqlGenerationServiceAS {
//
//    private final Model model;
//    private final SchemaRagService schemaRagService;
//
//    public SqlGenerationServiceAS(
//            Model model,
//            SchemaRagService schemaRagService) {
//        this.model = model;
//        this.schemaRagService = schemaRagService;
//    }
//
//    public String generateSql(String question) {
//        String context = schemaRagService.retrieveContext(question);
//
//        String prompt = PromptTemplates.SQL_SYSTEM + "\n\n"
//                + PromptTemplates.sqlUserPrompt(
//                question,
//                context.isBlank()
//                        ? "No additional context retrieved."
//                        : context
//        );
//
//
//        String generatedText = model.stream(
//                        List.of(
//                                UserMessage.builder()
//                                        .name("user")
//                                        .textContent(prompt)
//                                        .build()
//                        ),
//                        List.of(),
//                        null
//                )
//                .map(ChatResponse::getContent)
//                .filter(Objects::nonNull)
//                .flatMapIterable(content -> content)
//                .filter(TextBlock.class::isInstance)
//                .map(TextBlock.class::cast)
//                .map(TextBlock::getText)
//                .filter(Objects::nonNull)
//                .collect(Collectors.joining())
//                .block();
//
//        generatedText = generatedText == null ? "" : generatedText.trim();
//
//
//        System.out.println("Generated SQL: [" + generatedText + "]");
//
//        if (generatedText.isBlank()) {
//            throw new IllegalStateException(
//                    "AgentScope model returned an empty SQL response."
//            );
//        }
//
//        return generatedText;
//    }
//}
//
