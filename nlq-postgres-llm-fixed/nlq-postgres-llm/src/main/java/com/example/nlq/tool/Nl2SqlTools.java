////package com.example.nlq.tool;
////
////import com.example.nlq.security.SqlValidator;
////import com.example.nlq.service.SqlGenerationService;
////import io.agentscope.core.tool.Tool;
////import io.agentscope.core.tool.ToolParam;
////import org.springframework.stereotype.Component;
////
////@Component
////public class Nl2SqlTools {
////
////    private final SqlGenerationService sqlGenerationService;
////    private final SqlValidator sqlValidator;
////
////    public Nl2SqlTools(
////            SqlGenerationService sqlGenerationService,
////            SqlValidator sqlValidator) {
////        this.sqlGenerationService = sqlGenerationService;
////        this.sqlValidator = sqlValidator;
////    }
////
////    @Tool(
////            name = "generate_sql",
////            description = "Generate a PostgreSQL SELECT query for the user's natural-language question."
////    )
////    public String generateSql(
////            @ToolParam(
////                    name = "question",
////                    description = "The user's question about the database"
////            ) String question) {
////
////        return sqlGenerationService.generateSql(question);
////    }
////
////    @Tool(
////            name = "validate_sql",
////            description = "Validate and prepare a generated SQL query. Only SELECT queries referencing allowed application tables are accepted."
////    )
////    public String validateSql(
////            @ToolParam(
////                    name = "sql",
////                    description = "The SQL query that needs validation"
////            ) String sql) {
////
////        return sqlValidator.validateAndPrepare(sql);
////    }
////}
//
//
//
////package com.example.nlq.tool;
////
////import com.example.nlq.security.SqlValidator;
////import com.example.nlq.service.SqlGenerationServiceAS;
////import io.agentscope.core.tool.Tool;
////import io.agentscope.core.tool.ToolParam;
////import org.springframework.stereotype.Component;
////
////@Component
////public class Nl2SqlTools {
////
////    private final SqlGenerationServiceAS sqlGenerationServiceAS;
////    private final SqlValidator sqlValidator;
////
////    public Nl2SqlTools(
////            SqlGenerationServiceAS sqlGenerationServiceAS,
////            SqlValidator sqlValidator) {
////        this.sqlGenerationServiceAS = sqlGenerationServiceAS;
////        this.sqlValidator = sqlValidator;
////    }
////
////    @Tool(
////            name = "generate_sql",
////            description = "Generate a PostgreSQL SELECT query for the user's database question using AgentScope and the available schema context."
////    )
////    public String generateSql(
////            @ToolParam(
////                    name = "question",
////                    description = "The user's natural-language database question"
////            ) String question) {
////
////        return sqlGenerationServiceAS.generateSql(question);
////    }
////
////    @Tool(
////            name = "validate_sql",
////            description = "Validate a generated SQL query and return the safe prepared SQL. Reject SQL that fails validation."
////    )
////    public String validateSql(
////            @ToolParam(
////                    name = "sql",
////                    description = "The SQL query that needs validation"
////            ) String sql) {
////
////        return sqlValidator.validateAndPrepare(sql);
////    }
////}
//
//
//
//
//package com.example.nlq.tool;
//
//import com.example.nlq.security.SqlValidator;
//import com.example.nlq.service.SqlGenerationServiceAS;
//import io.agentscope.core.tool.Tool;
//import io.agentscope.core.tool.ToolParam;
//
//import java.util.Objects;
//import java.util.function.Consumer;
//
//public class Nl2SqlTools {
//
//    private final SqlGenerationServiceAS sqlGenerationServiceAS;
//    private final SqlValidator sqlValidator;
//    private final Consumer<String> validatedSqlConsumer;
//
//    public Nl2SqlTools(
//            SqlGenerationServiceAS sqlGenerationServiceAS,
//            SqlValidator sqlValidator,
//            Consumer<String> validatedSqlConsumer) {
//
//        this.sqlGenerationServiceAS =
//                Objects.requireNonNull(sqlGenerationServiceAS);
//        this.sqlValidator = Objects.requireNonNull(sqlValidator);
//        this.validatedSqlConsumer =
//                Objects.requireNonNull(validatedSqlConsumer);
//    }
//
//    @Tool(
//            name = "generate_sql",
//            description = "Generate a PostgreSQL SELECT query using the available schema context."
//    )
//    public String generateSql(
//            @ToolParam(
//                    name = "question",
//                    description = "The user's natural-language database question"
//            ) String question) {
//
//        return sqlGenerationServiceAS.generateSql(question);
//    }
//
//    @Tool(
//            name = "validate_sql",
//            description = "Validate SQL and return the safe prepared SQL. Reject SQL that fails validation."
//    )
//    public String validateSql(
//            @ToolParam(
//                    name = "sql",
//                    description = "The generated SQL query"
//            ) String sql) {
//
//        String safeSql = sqlValidator.validateAndPrepare(sql);
//        validatedSqlConsumer.accept(safeSql);
//        return safeSql;
//    }
//}
//
