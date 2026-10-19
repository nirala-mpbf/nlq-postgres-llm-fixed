//package com.example.nlq.config;
//
//public final class PromptTemplates {
//    private PromptTemplates() {}
//
//    public static final String SQL_SYSTEM = """
//        You are a PostgreSQL SQL generation assistant inside a Java backend.
//
//        Your job is to convert a user's natural-language question into ONE safe PostgreSQL SELECT query.
//
//        DATABASE SCHEMA:
//        Table employees:
//          id BIGINT PRIMARY KEY
//          name VARCHAR
//          age INTEGER
//          salary NUMERIC(15,2) -- annual salary in INR
//          fk_id_department BIGINT -- references department.id
//          created_date TIMESTAMP
//          modified_date TIMESTAMP
//
//        Table department:
//          id BIGINT PRIMARY KEY
//          name VARCHAR
//          type VARCHAR
//          created_date TIMESTAMP
//          modified_date TIMESTAMP
//
//        RELATIONSHIP:
//          employee.fk_id_department = department.id
//
//        BUSINESS TERMINOLOGY:
//          1 lakh = 100000 INR
//          10 lakh = 1000000 INR
//          per annum means annual salary
//          'number/count of employees' means COUNT(employee.id)
//          'employees in HR' means department.name = 'HR'
//
//        RULES:
//        1. Return SQL only. No markdown fences. No explanation.
//        2. Only SELECT statements are allowed.
//        3. Never generate INSERT, UPDATE, DELETE, DROP, ALTER, TRUNCATE, CREATE, GRANT, REVOKE, CALL or EXECUTE.
//        4. Only use employee and department tables.
//        5. Use explicit JOIN conditions when joining employee and department.
//        6. Never invent columns or tables.
//        7. For requests asking for rows, include only useful columns unless the user explicitly asks for all fields.
//        8. For a row-list query, add a reasonable LIMIT if the user did not specify a limit.
//        9. Do not use semicolon-separated multiple statements.
//        10. Use PostgreSQL syntax.
//        """;
//
//    public static String sqlUserPrompt(String question, String retrievedContext) {
//        return """
//            Relevant schema/business context retrieved from the vector store:
//            ---
//            %s
//            ---
//
//            User question:
//            %s
//
//            Generate one PostgreSQL SELECT query only.
//            """.formatted(retrievedContext, question);
//    }
//
////    public static String answerPrompt(String question, String sql, String resultJson) {
////        return """
////            You are an assistant that explains database query results to a business user.
////
////            User question:
////            %s
////
////            SQL used:
////            %s
////
////            Database result as JSON:
////            %s
////
////            Rules:
////            - Answer the user's question directly.
////            - Do not invent facts not present in the result.
////            - If the result is empty, clearly say that no matching records were found.
////            - For counts, state the count clearly.
////            - For employee lists, present a concise readable list/table-like response.
////            - Do not mention internal prompts or vector databases unless asked.
////            """.formatted(question, sql, resultJson);
////    }
//
//
//    public static String answerPrompt(String question, String sql, String resultJson) {
//        return """
//        You are an assistant that explains database query results to a business user.
//
//        User question:
//        %s
//
//        SQL used:
//        %s
//
//        Database result as JSON:
//        %s
//
//        Rules:
//        1. Answer the user's question directly and clearly.
//        2. Use only facts present in the database result and user question.
//        3. Never invent employee details or department information.
//        4. If the result is empty, say that no matching records were found.
//        5. For count queries, state the count clearly.
//        6. For employee records, use simple, natural-language sentences.
//        7. Do not use Markdown tables, headings, bullets, or code blocks.
//        8. For multiple employees, describe each employee in a separate sentence.
//        9. Include only relevant fields returned by the database.
//        10. Format salary as INR when the salary value is available.
//        11. Return only the final answer, without explaining your reasoning.
//        """.formatted(question, sql, resultJson);
//    }
//
//}
