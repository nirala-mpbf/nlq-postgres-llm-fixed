//package com.example.nlq.service;
//
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//import java.util.Map;
//
//@Service
//public class SqlExecutionService {
//
//    private final JdbcTemplate jdbcTemplate;
//
//    public SqlExecutionService(JdbcTemplate jdbcTemplate) {
//        this.jdbcTemplate = jdbcTemplate;
//    }
//
////    public List<Map<String, Object>> execute(String sql) {
////        return jdbcTemplate.queryForList(sql);
////    }
//
//    public List<Map<String, Object>> execute(String sql) {
//        System.out.println("SQL RECEIVED BY DATABASE: [" + sql + "]");
//        return jdbcTemplate.queryForList(sql);
//    }
//
//}
