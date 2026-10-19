package com.example.nlq.service;

import com.example.nlq.dto.PromptResponse;
import com.example.nlq.dto.SavePromptResponse;
import com.example.nlq.exception.LakefsException;
import io.lakefs.clients.sdk.ApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

@Service
public class PromptService {

    private static final Pattern NAME = Pattern.compile("[a-zA-Z0-9_-]{1,100}");
    private static final Pattern REF = Pattern.compile("[a-zA-Z0-9_-]{1,200}");
    private final LakeFSService lakeFS;
    private final String repository;
    private final String branch;

    public PromptService(LakeFSService lakeFS,
                         @Value("${lakefs.repository}") String repository,
                         @Value("${lakefs.branch}") String branch) {
        this.lakeFS = lakeFS;
        this.repository = repository;
        this.branch = branch;
    }

    public SavePromptResponse save(String workflow, String kind, String markdown) throws ApiException {
        String path = path(workflow, kind);
        if (markdown == null || markdown.isBlank()) {
            throw new LakefsException(HttpStatus.BAD_REQUEST, "Markdown content must not be blank");
        }
        if (markdown.getBytes(StandardCharsets.UTF_8).length > 1_048_576) {
            throw new LakefsException(HttpStatus.PAYLOAD_TOO_LARGE, "Markdown exceeds 1 MB");
        }
        String commitId = lakeFS.uploadAndCommit(repository, branch, path, markdown);
        return new SavePromptResponse(workflow + "/" + kind, commitId, "Saved " + path);
    }

    public SavePromptResponse save(String workflow, String kind, Resource markdownFile) throws ApiException {
        if (markdownFile == null) {
            throw new LakefsException(HttpStatus.BAD_REQUEST, "Markdown resource is required");
        }
        try (var input = markdownFile.getInputStream()) {
            byte[] bytes = input.readNBytes(1_048_577);
            if (bytes.length > 1_048_576) {
                throw new LakefsException(HttpStatus.PAYLOAD_TOO_LARGE, "Markdown exceeds 1 MB");
            }
            return save(workflow, kind, new String(bytes, StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new LakefsException(HttpStatus.BAD_REQUEST, "Cannot read Markdown resource", e);
        }
    }

    public PromptResponse latest(String workflow, String kind) {
        return read(workflow, kind, branch);
    }

    public PromptResponse version(String workflow, String kind, String commitId) {
        if (commitId == null || !REF.matcher(commitId).matches()) {
            throw new LakefsException(HttpStatus.BAD_REQUEST, "Invalid lakeFS reference");
        }
        return read(workflow, kind, commitId);
    }

    private PromptResponse read(String workflow, String kind, String ref) {
        return new PromptResponse(workflow + "/" + kind, ref,
                lakeFS.download(repository, ref, path(workflow, kind)));
    }

    private static String path(String workflow, String kind) {
        if (workflow == null || kind == null || !NAME.matcher(workflow).matches() || !NAME.matcher(kind).matches()) {
            throw new LakefsException(HttpStatus.BAD_REQUEST, "Invalid workflow or prompt name");
        }
        return "prompts/" + workflow + "/" + kind + ".md";
    }
}
