//package com.example.nlq.config;
//
//
//import com.example.nlq.tool.Nl2SqlTools;
//import io.agentscope.core.model.Model;
//import io.agentscope.core.tool.Toolkit;
//import io.agentscope.extensions.model.ollama.OllamaChatModel;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class AgentScopeConfig {
//    @Bean
//    public Model agentScopeModel() {
//        return OllamaChatModel.builder()
//                .baseUrl("http://localhost:11434")
//                .modelName("llama3.1:8b")
//                .build();
//    }
//
//    @Bean
//    public Toolkit nl2SqlToolkit(Nl2SqlTools nl2SqlTools) {
//        Toolkit toolkit = new Toolkit();
//        toolkit.registerTool(nl2SqlTools);
//        return toolkit;
//    }
//}



package com.example.nlq.config;

import io.agentscope.core.model.Model;
import io.agentscope.extensions.model.ollama.OllamaChatModel;
import io.agentscope.extensions.model.ollama.options.OllamaOptions;
import io.agentscope.extensions.model.openai.OpenAIChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AgentScopeConfig {

    @Bean
    public Model agentScopeModel() {

//        OllamaOptions options = OllamaOptions.builder()
//                .numCtx(2048)
//                .numPredict(512)
//                .temperature(0.1)
//                .build();

        return OpenAIChatModel.builder()
                .apiKey("local-gateway")
                .modelName("llama3.1:8b")
                .baseUrl("http://localhost:4000/v1")
                .stream(true)
                .build();
    }
}
