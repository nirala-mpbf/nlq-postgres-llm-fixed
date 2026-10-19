//
//package com.example.nlq.tool;
//
//import com.example.nlq.dto.QueryResponse;
//import com.example.nlq.security.SqlValidator;
//import com.example.nlq.service.AnswerGenerationServiceAS;
//import com.example.nlq.service.SqlExecutionService;
//import com.example.nlq.service.SqlGenerationServiceAS;
//import io.agentscope.core.tool.Tool;
//import io.agentscope.core.tool.ToolParam;
//
//import java.util.List;
//import java.util.Map;
//import java.util.function.Consumer;
//
//public class Nl2SqlWorkflowTool {
//
//    private final SqlGenerationServiceAS sqlGenerationServiceAS;
//    private final SqlValidator sqlValidator;
//    private final SqlExecutionService sqlExecutionService;
//    private final AnswerGenerationServiceAS answerGenerationServiceAS;
//    private final Consumer<QueryResponse> responseConsumer;
//
//    public Nl2SqlWorkflowTool(
//            SqlGenerationServiceAS sqlGenerationServiceAS,
//            SqlValidator sqlValidator,
//            SqlExecutionService sqlExecutionService,
//            AnswerGenerationServiceAS answerGenerationServiceAS,
//            Consumer<QueryResponse> responseConsumer) {
//
//        this.sqlGenerationServiceAS = sqlGenerationServiceAS;
//        this.sqlValidator = sqlValidator;
//        this.sqlExecutionService = sqlExecutionService;
//        this.answerGenerationServiceAS = answerGenerationServiceAS;
//        this.responseConsumer = responseConsumer;
//    }
//
////    @Tool(
////            name = "run_nl2sql",
////            description = """
////            Process a database question end to end.
////            Generate PostgreSQL SQL, validate it, execute it,
////            and generate an answer from the actual database rows.
////            Stop if any step fails.
////            """
////    )
////    public String runNl2Sql(
////            @ToolParam(
////                    name = "question",
////                    description = "The user's natural-language database question"
////            ) String question) {
////
////        if (question == null || question.isBlank()) {
////            throw new IllegalArgumentException(
////                    "question is required."
////            );
////        }
////
////        String generatedSql =
////                sqlGenerationServiceAS.generateSql(question);
////
////        String safeSql =
////                sqlValidator.validateAndPrepare(generatedSql);
////
////        List<Map<String, Object>> rows =
////                sqlExecutionService.execute(safeSql);
////
////        String answer =
////                answerGenerationServiceAS.generate(
////                        question,
////                        safeSql,
////                        rows
////                );
////
////        QueryResponse result = new QueryResponse(
////                question,
////                answer,
////                safeSql,
////                rows
////        );
////
////        responseConsumer.accept(result);
////
////        return "NL2SQL workflow completed successfully. "
////                + "Database rows retrieved: " + rows.size()
////                + ". Answer generated successfully.";
////    }
//
//
//    @Tool(
//            name = "run_nl2sql",
//            description = """
//        Process a database question end to end:
//        generate PostgreSQL SQL, validate it, execute it,
//        and generate an answer from actual database rows.
//        """
//    )
//    public String runNl2Sql(
//            @ToolParam(
//                    name = "question",
//                    description = "The user's natural-language database question"
//            ) String question) {
//
//        if (question == null || question.isBlank()) {
//            throw new IllegalArgumentException("question is required.");
//        }
//
//        long totalStart = System.nanoTime();
//
//        long stepStart = System.nanoTime();
//        String generatedSql = sqlGenerationServiceAS.generateSql(question);
//        System.out.printf(
//                "NL2SQL_TIMING generate_sql=%.2f sec%n",
//                (System.nanoTime() - stepStart) / 1_000_000_000.0
//        );
//
//        stepStart = System.nanoTime();
//        String safeSql = sqlValidator.validateAndPrepare(generatedSql);
//        System.out.printf(
//                "NL2SQL_TIMING validate_sql=%.2f sec%n",
//                (System.nanoTime() - stepStart) / 1_000_000_000.0
//        );
//
//        stepStart = System.nanoTime();
//        List<Map<String, Object>> rows = sqlExecutionService.execute(safeSql);
//        System.out.printf(
//                "NL2SQL_TIMING execute_sql=%.2f sec%n",
//                (System.nanoTime() - stepStart) / 1_000_000_000.0
//        );
//
//        stepStart = System.nanoTime();
//        String answer = answerGenerationServiceAS.generate(
//                question, safeSql, rows
//        );
//        System.out.printf(
//                "NL2SQL_TIMING generate_answer=%.2f sec%n",
//                (System.nanoTime() - stepStart) / 1_000_000_000.0
//        );
//
//        QueryResponse result = new QueryResponse(
//                question, answer, safeSql, rows
//        );
//        responseConsumer.accept(result);
//
//        System.out.printf(
//                "NL2SQL_TIMING workflow_total=%.2f sec%n",
//                (System.nanoTime() - totalStart) / 1_000_000_000.0
//        );
//
//        return "NL2SQL workflow completed successfully. "
//                + "Database rows retrieved: " + rows.size()
//                + ". Answer generated successfully.";
//    }
//
//
//
//}
