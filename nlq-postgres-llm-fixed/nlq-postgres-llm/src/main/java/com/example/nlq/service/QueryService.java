//package com.example.nlq.service;
//
//import com.example.nlq.dto.QueryResponse;
//import com.example.nlq.security.SqlValidator;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//import java.util.Map;
//
//@Service
//public class QueryService {
//
//    private final SqlGenerationService sqlGenerationService;
//    private final SqlValidator sqlValidator;
//    private final SqlExecutionService sqlExecutionService;
//    private final AnswerGenerationService answerGenerationService;
//
//    public QueryService(
//            SqlGenerationService sqlGenerationService,
//            SqlValidator sqlValidator,
//            SqlExecutionService sqlExecutionService,
//            AnswerGenerationService answerGenerationService) {
//        this.sqlGenerationService = sqlGenerationService;
//        this.sqlValidator = sqlValidator;
//        this.sqlExecutionService = sqlExecutionService;
//        this.answerGenerationService = answerGenerationService;
//    }
//
//    public QueryResponse process(String question) {
//        String generatedSql = sqlGenerationService.generateSql(question);
//        String safeSql = sqlValidator.validateAndPrepare(generatedSql);
//        List<Map<String, Object>> rows = sqlExecutionService.execute(safeSql);
//        String answer = answerGenerationService.generate(question, safeSql, rows);
//
//        return new QueryResponse(question, answer, safeSql, rows);
//    }
//}
