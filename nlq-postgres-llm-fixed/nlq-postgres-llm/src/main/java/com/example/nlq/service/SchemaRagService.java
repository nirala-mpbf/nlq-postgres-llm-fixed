//package com.example.nlq.service;
//
//import dev.langchain4j.data.document.Metadata;
//import dev.langchain4j.data.embedding.Embedding;
//import dev.langchain4j.data.segment.TextSegment;
//import dev.langchain4j.model.embedding.EmbeddingModel;
//import dev.langchain4j.store.embedding.EmbeddingMatch;
//import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
//import dev.langchain4j.store.embedding.EmbeddingStore;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.ApplicationArguments;
//import org.springframework.boot.ApplicationRunner;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Service
//public class SchemaRagService implements ApplicationRunner {
//
//    private final EmbeddingModel embeddingModel;
//    private final EmbeddingStore<TextSegment> embeddingStore;
//    private final int topK;
//    private final double minScore;
//
//    private static final List<String> SCHEMA_DOCUMENTS = List.of(
//            """
//            DATABASE: employees table
//            The employee table contains employee master data.
//            Columns:
//            id = unique employee identifier
//            name = employee full name
//            age = employee age in years
//            salary = annual salary in INR
//            fk_id_department = foreign key to department.id
//            created_date = record creation timestamp
//            modified_date = record modification timestamp
//            """,
//            """
//            DATABASE: department table
//            The department table contains department master data.
//            Columns:
//            id = unique department identifier
//            name = department name such as HR, IT, Finance or Sales
//            type = department type/category
//            created_date = record creation timestamp
//            modified_date = record modification timestamp
//            """,
//            """
//            DATABASE RELATIONSHIP
//            employees.fk_id_department references department.id.
//            To find an employee's department or filter employees by department,
//            join employee e to department d using e.fk_id_department = d.id.
//            """,
//            """
//            BUSINESS TERMINOLOGY
//            1 lakh = 100000 INR.
//            10 lakh = 1000000 INR.
//            Per annum means annual salary.
//            'number of employees', 'employee count' and 'no. of employees' mean COUNT(employee.id).
//            'employees in HR' means department.name = 'HR'.
//            """
//    );
//
//    public SchemaRagService(
//            EmbeddingModel embeddingModel,
//            EmbeddingStore<TextSegment> embeddingStore,
//            @Value("${app.rag.top-k:5}") int topK,
//            @Value("${app.rag.min-score:0.55}") double minScore) {
//        this.embeddingModel = embeddingModel;
//        this.embeddingStore = embeddingStore;
//        this.topK = topK;
//        this.minScore = minScore;
//    }
//
//    @Override
//    public void run(ApplicationArguments args) {
//        // This project owns the schema knowledge table, so rebuild it on startup.
//        // For production, replace this with versioned/upserted documents.
//        embeddingStore.removeAll();
//
//        for (String document : SCHEMA_DOCUMENTS) {
//            TextSegment segment = TextSegment.from(document, Metadata.from("source", "database-schema"));
//            Embedding embedding = embeddingModel.embed(document).content();
//            embeddingStore.add(embedding, segment);
//        }
//    }
//
//    public String retrieveContext(String question) {
//        Embedding queryEmbedding = embeddingModel.embed(question).content();
//        EmbeddingSearchRequest request = EmbeddingSearchRequest.builder()
//                .queryEmbedding(queryEmbedding)
//                .maxResults(topK)
//                .minScore(minScore)
//                .build();
//
//        return embeddingStore.search(request).matches().stream()
//                .map(EmbeddingMatch::embedded)
//                .map(TextSegment::text)
//                .collect(Collectors.joining("\n\n"));
//    }
//}
