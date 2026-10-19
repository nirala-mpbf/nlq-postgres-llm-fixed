//package com.example.nlq.controller;
//
//import com.example.nlq.dto.*;
//import com.example.nlq.exception.LakefsException;
//import com.example.nlq.service.PromptService;
//import io.lakefs.clients.sdk.ApiException;
//import jakarta.validation.Valid;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.util.Locale;
//
//@RestController
//@RequestMapping("/api/v1/prompts")
//public class promptOPSController {
//
//    private final PromptService prompts;
//
//    public promptOPSController(PromptService prompts) {
//        this.prompts = prompts;
//    }
//
//    @PostMapping(path = "/save", consumes = MediaType.APPLICATION_JSON_VALUE)
//    public SavePromptResponse saveText(@Valid @RequestBody SaveMarkdownRequest request) throws ApiException {
//
//        return prompts.save(request.workflow(), request.kind(), request.content());
//    }
//
//    @PostMapping(path = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public SavePromptResponse uploadFile(@RequestPart("file") MultipartFile file, @Valid @RequestPart("metadata") PromptMetadataRequest request) throws ApiException {
//
//        if (file.isEmpty()) {
//            throw new LakefsException(HttpStatus.BAD_REQUEST, "Markdown file must not be empty");
//        }
//
//        String filename = file.getOriginalFilename();
//
//        if (filename == null || !filename.toLowerCase(Locale.ROOT).endsWith(".md")) {
//            throw new LakefsException(HttpStatus.BAD_REQUEST, "Only .md files are accepted");
//        }
//
//        return prompts.save(request.workflow(), request.kind(), file.getResource());
//    }
//
//    @PostMapping(path = "/latest", consumes = MediaType.APPLICATION_JSON_VALUE)
//    public PromptResponse latest(@Valid @RequestBody PromptLookupRequest request) {
//
//        return prompts.latest(request.workflow(), request.kind());
//    }
//
//    @PostMapping(path = "/version", consumes = MediaType.APPLICATION_JSON_VALUE)
//    public PromptResponse version(@Valid @RequestBody PromptVersionRequest request) {
//
//        return prompts.version(request.workflow(), request.kind(), request.commitId());
//    }
//}