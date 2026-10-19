//package com.example.nlq.service;
//
//import com.example.nlq.config.PromptTemplates;
//import dev.langchain4j.model.chat.ChatModel;
//import org.springframework.stereotype.Service;
//
//@Service
//public class SqlGenerationService {
//
//    private final ChatModel chatModel;
//    private final SchemaRagService schemaRagService;
//
//    public SqlGenerationService(ChatModel chatModel, SchemaRagService schemaRagService) {
//        this.chatModel = chatModel;
//        this.schemaRagService = schemaRagService;
//    }
//
//    public String generateSql(String question) {
//        String context = schemaRagService.retrieveContext(question);
//        String prompt = PromptTemplates.SQL_SYSTEM + "\n\n" +
//                PromptTemplates.sqlUserPrompt(question, context.isBlank() ? "No additional context retrieved." : context);
////        return chatModel.generate(prompt).trim();
////        return chatModel.chat(prompt).trim();
//
//        String sql = chatModel.chat(prompt).trim();
//
//        System.out.println("Generated SQL: [" + sql + "]");
//
//        return sql;
//    }
//}
