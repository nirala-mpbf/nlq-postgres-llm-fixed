package com.example.nlq.config;

import io.lakefs.clients.sdk.ApiClient;
import io.lakefs.clients.sdk.auth.HttpBasicAuth;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.http.HttpClient;
import java.time.Duration;

@Configuration
public class LakeFsClientConfig {

    @Bean
    public ApiClient lakeFsApiClient(
            @Value("${lakefs.endpoint}") String endpoint,
            @Value("${lakefs.access-key}") String accessKey,
            @Value("${lakefs.secret-key}") String secretKey) {

        ApiClient client = new ApiClient();
        client.setBasePath(endpoint.replaceAll("/+$", "") + "/api/v1");

        HttpBasicAuth auth = (HttpBasicAuth) client.getAuthentication("basic_auth");
        auth.setUsername(accessKey);
        auth.setPassword(secretKey);

        return client;
    }

    @Bean
    public HttpClient lakeFsDownloadHttpClient() {
        return HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .version(HttpClient.Version.HTTP_1_1)
                .build();
    }

}
