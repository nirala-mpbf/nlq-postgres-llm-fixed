//package com.example.nlq.controller;
//
//import io.agentscope.core.ReActAgent;
//import io.agentscope.core.message.Msg;
//import io.agentscope.core.message.MsgRole;
//import io.agentscope.core.message.TextBlock;
//import io.agentscope.core.model.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//public class AgentTestController {
//
//    private final Model model;
//
//    public AgentTestController(Model model) {
//        this.model = model;
//    }
//
//    @GetMapping("/api/v1/agent-test")
//    public String testAgent(
//            @RequestParam(defaultValue = "Explain what SQL is in two sentences") String prompt) {
//
//        ReActAgent agent = ReActAgent.builder()
//                .name("NL2SQLAgent")
//                .sysPrompt("You are a helpful assistant for a natural-language-to-SQL application.")
//                .model(model)
//                .build();
//
//        Msg userMessage = Msg.builder()
//                .name("User")
//                .role(MsgRole.USER)
//                .content(TextBlock.builder().text(prompt).build())
//                .build();
//
//        Msg response = agent.call(userMessage).block();
//
//        if (response == null || response.getTextContent() == null) {
//            return "No response received from AgentScope";
//        }
//
//        return response.getTextContent();
//    }
//}
//
