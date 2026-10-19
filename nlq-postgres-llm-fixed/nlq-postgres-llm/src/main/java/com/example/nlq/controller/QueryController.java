//package com.example.nlq.controller;
//
//import com.example.nlq.dto.QueryRequest;
//import com.example.nlq.dto.QueryResponse;
//import com.example.nlq.service.QueryService;
//import jakarta.validation.Valid;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping("/api/v1/query")
//public class QueryController {
//
//    private final QueryService queryService;
//
//    public QueryController(QueryService queryService) {
//        this.queryService = queryService;
//    }
//
//    @PostMapping
//    public QueryResponse query(@Valid @RequestBody QueryRequest request) {
//        return queryService.process(request.question());
//    }
//}
