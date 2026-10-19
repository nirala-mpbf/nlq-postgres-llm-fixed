//package com.example.nlq.service;
//import com.example.nlq.dto.QueryResponse;
//import com.example.nlq.security.SqlValidator;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//import java.util.Map;
//
//@Service
//public class QueryServiceAS {
//
//    private final SqlGenerationServiceAS sqlGenerationServiceAS;
//    private final SqlValidator sqlValidator;
//    private final SqlExecutionService sqlExecutionService;
//    private final AnswerGenerationServiceAS answerGenerationServiceAS;
//
//    public QueryServiceAS(
//            SqlGenerationServiceAS sqlGenerationServiceAS,
//            SqlValidator sqlValidator,
//            SqlExecutionService sqlExecutionService,
//            AnswerGenerationServiceAS answerGenerationServiceAS) {
//
//        this.sqlGenerationServiceAS = sqlGenerationServiceAS;
//        this.sqlValidator = sqlValidator;
//        this.sqlExecutionService = sqlExecutionService;
//        this.answerGenerationServiceAS = answerGenerationServiceAS;
//    }
//
//    public QueryResponse process(String question) {
//
//        String generatedSql =
//                sqlGenerationServiceAS.generateSql(question);
//
//        String safeSql =
//                sqlValidator.validateAndPrepare(generatedSql);
//
//        List<Map<String, Object>> rows =
//                sqlExecutionService.execute(safeSql);
//
//        String answer =
//                answerGenerationServiceAS.generate(
//                        question, safeSql, rows
//                );
//
//        return new QueryResponse(question, answer, safeSql, rows);
//    }
//}
//
