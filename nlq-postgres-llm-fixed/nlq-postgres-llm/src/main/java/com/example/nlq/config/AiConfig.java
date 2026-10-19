//package com.example.nlq.config;
//
//import dev.langchain4j.model.chat.ChatModel;
//import dev.langchain4j.model.embedding.EmbeddingModel;import dev.langchain4j.store.embedding.EmbeddingStore;
//import dev.langchain4j.store.embedding.pgvector.MetadataStorageConfig;
//import dev.langchain4j.model.ollama.OllamaChatModel;
//import dev.langchain4j.model.ollama.OllamaEmbeddingModel;
//
//import dev.langchain4j.store.embedding.pgvector.PgVectorEmbeddingStore;
//import dev.langchain4j.data.segment.TextSegment;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import javax.sql.DataSource;
//
//@Configuration
//public class AiConfig {
//
//    @Bean
//    public ChatModel chatModel(
//            @Value("${app.ollama.base-url}") String baseUrl,
//            @Value("${app.ollama.chat-model}") String modelName,
//            @Value("${app.ollama.temperature:0.0}") double temperature) {
//
//        return OllamaChatModel.builder()
//                .baseUrl(baseUrl)
//                .modelName(modelName)
//                .temperature(temperature)
//                .build();
//    }
//
//    @Bean
//    public EmbeddingModel embeddingModel(
//            @Value("${app.ollama.base-url}") String baseUrl,
//            @Value("${app.ollama.embedding-model}") String modelName) {
//
//        return OllamaEmbeddingModel.builder()
//                .baseUrl(baseUrl)
//                .modelName(modelName)
//                .build();
//    }
//
//    @Bean
//    EmbeddingStore<TextSegment> embeddingStore(
//            DataSource dataSource,
//            EmbeddingModel embeddingModel,
//            @Value("${app.rag.vector-table:nlq_schema_embeddings}") String tableName) {
//
////        return PgVectorEmbeddingStore.datasourceBuilder()
////                .datasource(dataSource)
////                .table(tableName)
////                .dimension(embeddingModel.dimension())
////                .createTable(true)
////                .useIndex(false)
////                .metadataStorageConfig(MetadataStorageConfig.combinedJsonb())
////                .build();
//
//        return PgVectorEmbeddingStore.datasourceBuilder()
//                .datasource(dataSource)
//                .table(tableName)
//                .dimension(embeddingModel.dimension())
//                .createTable(true)
//                .useIndex(false)
//                .build();
//    }
//}
