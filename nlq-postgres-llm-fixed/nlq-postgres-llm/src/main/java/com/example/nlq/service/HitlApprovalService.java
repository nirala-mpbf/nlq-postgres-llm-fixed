//package com.example.nlq.service;
//
//import com.example.nlq.security.SqlValidator;
//import org.springframework.stereotype.Service;
//
//import java.time.Instant;
//import java.util.Map;
//import java.util.UUID;
//import java.util.concurrent.ConcurrentHashMap;
//
//@Service
//public class HitlApprovalService {
//
//    public enum Status {
//        PENDING,
//        APPROVED,
//        REJECTED,
//        COMPLETED,
//        FAILED
//    }
//
//    public record PendingRequest(
//            String requestId,
//            String question,
//            String sql,
//            Status status,
//            Instant createdAt
//    ) {}
//
//    private final Map<String, PendingRequest> requests =
//            new ConcurrentHashMap<>();
//
//    private final SqlValidator sqlValidator;
//
//    public HitlApprovalService(SqlValidator sqlValidator) {
//        this.sqlValidator = sqlValidator;
//    }
//
//    public PendingRequest createPendingRequest(
//            String question,
//            String validatedSql) {
//
//        String safeSql =
//                sqlValidator.validateAndPrepare(validatedSql);
//
//        String requestId = UUID.randomUUID().toString();
//
//        PendingRequest request = new PendingRequest(
//                requestId,
//                question,
//                safeSql,
//                Status.PENDING,
//                Instant.now()
//        );
//
//        requests.put(requestId, request);
//
//        return request;
//    }
//
//    public PendingRequest getPendingRequest(String requestId) {
//        PendingRequest request = requests.get(requestId);
//
//        if (request == null) {
//            throw new IllegalArgumentException(
//                    "Request not found: " + requestId
//            );
//        }
//
//        return request;
//    }
//}
//
