package com.example.nlq.config;

import io.agentscope.core.tool.mcp.McpClientBuilder;
import io.agentscope.core.tool.mcp.McpClientWrapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class McpClientConfig {

    @Bean
    public McpClientWrapper brainWorkflowMcpClient(
            @Value("${mcp.block.url:http://localhost:8082/mcp}")
            String mcpUrl) {

        return McpClientBuilder.create("brain-workflow-tools")
                .streamableHttpTransport(mcpUrl)
                .buildAsync()
                .block();
    }
}

