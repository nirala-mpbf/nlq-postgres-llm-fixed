//package com.example.nlq.service;
//
//import com.example.nlq.config.PromptTemplates;
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import dev.langchain4j.model.chat.ChatModel;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//import java.util.Map;
//
//@Service
//public class AnswerGenerationService {
//
//    private final ChatModel chatModel;
//    private final ObjectMapper objectMapper;
//
//    public AnswerGenerationService(ChatModel chatModel, ObjectMapper objectMapper) {
//        this.chatModel = chatModel;
//        this.objectMapper = objectMapper;
//    }
//
//    public String generate(String question, String sql, List<Map<String, Object>> rows) {
//        try {
//            String json = objectMapper.writeValueAsString(rows);
////            return chatModel.generate(PromptTemplates.answerPrompt(question, sql, json)).trim();
//            return chatModel
//                    .chat(PromptTemplates.answerPrompt(question, sql, json))
//                    .trim();
//        } catch (JsonProcessingException e) {
//            throw new IllegalStateException("Could not serialize database result", e);
//        }
//    }
//}
