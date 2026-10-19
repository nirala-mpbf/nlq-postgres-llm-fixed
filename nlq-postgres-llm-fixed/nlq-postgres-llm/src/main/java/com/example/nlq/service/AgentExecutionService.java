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
//public class AgentExecutionService {
//
//    private final HitlApprovalService hitlApprovalService;
//    private final SqlValidator sqlValidator;
//    private final SqlExecutionService sqlExecutionService;
//    private final AnswerGenerationServiceAS answerGenerationServiceAS;
//
//    public AgentExecutionService(
//            HitlApprovalService hitlApprovalService,
//            SqlValidator sqlValidator,
//            SqlExecutionService sqlExecutionService,
//            AnswerGenerationServiceAS answerGenerationServiceAS) {
//
//        this.hitlApprovalService = hitlApprovalService;
//        this.sqlValidator = sqlValidator;
//        this.sqlExecutionService = sqlExecutionService;
//        this.answerGenerationServiceAS = answerGenerationServiceAS;
//    }
//
//    public QueryResponse executePreparedRequest(String requestId) {
//
//        HitlApprovalService.PendingRequest request =
//                hitlApprovalService.getPendingRequest(requestId);
//
//        if (request.status() != HitlApprovalService.Status.PENDING) {
//            throw new IllegalStateException(
//                    "Request is not pending: " + request.status()
//            );
//        }
//
//        String safeSql =
//                sqlValidator.validateAndPrepare(request.sql());
//
//        List<Map<String, Object>> rows =
//                sqlExecutionService.execute(safeSql);
//
//        String answer =
//                answerGenerationServiceAS.generate(
//                        request.question(),
//                        safeSql,
//                        rows
//                );
//
//        return new QueryResponse(
//                request.question(),
//                answer,
//                safeSql,
//                rows
//        );
//    }
//}
//
