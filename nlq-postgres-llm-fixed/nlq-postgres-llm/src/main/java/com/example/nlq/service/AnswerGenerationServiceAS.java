//package com.example.nlq.service;
//
//import com.example.nlq.config.PromptTemplates;
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
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
//public class AnswerGenerationServiceAS {
//
//    private final Model model;
//    private final ObjectMapper objectMapper;
//
//    public AnswerGenerationServiceAS(
//            Model model,
//            ObjectMapper objectMapper) {
//        this.model = model;
//        this.objectMapper = objectMapper;
//    }
//
//    public String generate(
//            String question,
//            String sql,
//            List<java.util.Map<String, Object>> rows) {
//
//        try {
//            String json = objectMapper.writeValueAsString(rows);
//
//            String prompt = PromptTemplates.answerPrompt(
//                    question,
//                    sql,
//                    json
//            );
//
//            String answer = model.stream(
//                            List.of(
//                                    UserMessage.builder()
//                                            .name("user")
//                                            .textContent(prompt)
//                                            .build()
//                            ),
//                            List.of(),
//                            null
//                    )
//                    .map(ChatResponse::getContent)
//                    .filter(Objects::nonNull)
//                    .flatMapIterable(content -> content)
//                    .filter(TextBlock.class::isInstance)
//                    .map(TextBlock.class::cast)
//                    .map(TextBlock::getText)
//                    .filter(Objects::nonNull)
//                    .collect(Collectors.joining())
//                    .block();
//
//            if (answer == null || answer.isBlank()) {
//                throw new IllegalStateException(
//                        "AgentScope returned an empty answer."
//                );
//            }
//
//            return answer.trim();
//
//        } catch (JsonProcessingException e) {
//            throw new IllegalStateException(
//                    "Could not serialize database result", e
//            );
//        }
//    }
//}
//
