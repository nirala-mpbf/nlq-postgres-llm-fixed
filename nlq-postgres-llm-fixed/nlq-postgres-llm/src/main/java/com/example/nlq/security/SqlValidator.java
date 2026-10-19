package com.example.nlq.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.regex.Pattern;

@Component
public class SqlValidator {

    private static final Pattern DISALLOWED = Pattern.compile(
            "\\b(insert|update|delete|drop|alter|truncate|create|grant|revoke|merge|call|execute|copy|vacuum|analyze|refresh|do)\\b",
            Pattern.CASE_INSENSITIVE);

    private static final Pattern FORBIDDEN_OBJECTS = Pattern.compile(
            "\\b(information_schema|pg_catalog|pg_toast|pg_temp|pg_internal)\\b",
            Pattern.CASE_INSENSITIVE);

//    private static final Pattern ALLOWED_TABLES = Pattern.compile(
//            "\\b(employees|department)\\b",
//            Pattern.CASE_INSENSITIVE);

    private static final Pattern ALLOWED_TABLES = Pattern.compile(
            "\\b(employees|department)\\b",
            Pattern.CASE_INSENSITIVE
    );

    private final int maxRows;

    public SqlValidator(@Value("${app.sql.max-rows:500}") int maxRows) {
        this.maxRows = maxRows;
    }

    public String validateAndPrepare(String rawSql) {
        if (rawSql == null || rawSql.isBlank()) {
            throw new IllegalArgumentException("LLM returned empty SQL");
        }

        String sql = rawSql.trim();
//        if (sql.startsWith("```") || sql.contains("```")) {
//            sql = sql.replace("```sql", "").replace("```SQL", "").replace("```", "").trim();
//        }
//
//        if (sql.contains(";") || sql.contains("--") || sql.contains("/*") || sql.contains("*/")) {
//            throw new IllegalArgumentException("SQL comments and multiple statements are not allowed");
//        }


        if (sql.startsWith("```") || sql.contains("```")) {
            sql = sql.replace("```sql", "")
                    .replace("```SQL", "")
                    .replace("```", "")
                    .trim();
        }

// Allow one optional trailing semicolon, but never multiple statements.
        if (sql.endsWith(";")) {
            sql = sql.substring(0, sql.length() - 1).trim();
        }

        if (sql.contains(";") || sql.contains("--")
                || sql.contains("/*") || sql.contains("*/")) {
            throw new IllegalArgumentException(
                    "SQL comments and multiple statements are not allowed"
            );
        }


        if (!sql.regionMatches(true, 0, "SELECT", 0, 6)) {
            throw new IllegalArgumentException("Only SELECT statements are allowed");
        }

        if (DISALLOWED.matcher(sql).find()) {
            throw new IllegalArgumentException("Disallowed SQL operation detected");
        }

        if (FORBIDDEN_OBJECTS.matcher(sql).find()) {
            throw new IllegalArgumentException("Access to PostgreSQL system schemas is not allowed");
        }

        // Force a whitelist: the generated SQL may only reference the two application tables.
        String withoutQuotedLiterals = sql.replaceAll("'([^']|'')*'", "''");
        var identifiers = Pattern.compile("\\b(?:from|join)\\s+([a-zA-Z_][a-zA-Z0-9_.]*)", Pattern.CASE_INSENSITIVE)
                .matcher(withoutQuotedLiterals);
        while (identifiers.find()) {
            String table = identifiers.group(1).toLowerCase(Locale.ROOT);
            if (table.contains(".")) {
                table = table.substring(table.lastIndexOf('.') + 1);
            }
            if (!table.equals("employees") && !table.equals("department")) {
                throw new IllegalArgumentException(
                        "SQL references a table outside the allowed schema: " + table
                );
            }
        }

        if (!ALLOWED_TABLES.matcher(withoutQuotedLiterals).find()) {
            throw new IllegalArgumentException("SQL does not reference an allowed application table");
        }

        // Prevent an accidental unbounded result set. Aggregate queries are unaffected semantically.
        if (!Pattern.compile("\\blimit\\b", Pattern.CASE_INSENSITIVE).matcher(sql).find()) {
            sql = sql + " LIMIT " + maxRows;
        }

        System.out.println("Final SQL: [" + sql + "]");

        return sql;
    }
}
