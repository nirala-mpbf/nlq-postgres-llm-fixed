package com.example.nlq.service;

import com.example.nlq.OnboardingStep;
import com.example.nlq.dto.PromptResponse;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class OnboardingPromptService {

    private static final String WORKFLOW = "ddod";

    private static final Pattern METADATA_BLOCK =
            Pattern.compile(
                    "(?m)^---[ \\t]*\\R"
                            + "referenceId:[ \\t]*([A-Za-z0-9_-]{1,200})[ \\t]*\\R"
                            + "---[ \\t]*\\R?"
            );

    private final PromptService promptService;

    public OnboardingPromptService(PromptService promptService) {
        this.promptService = promptService;
    }

    public LoadedPrompt loadPromptDetails(OnboardingStep step) {

        if (step == null) {
            throw new IllegalArgumentException(
                    "Onboarding step is required."
            );
        }

        String kind = switch (step) {
            case OTP_VERIFICATION -> "otp";
            case PROFILE_DETAILS -> "profileDetails";
            case ACCOUNT_STATEMENT -> "accountStatement";
            case UDYAM_DOCUMENT -> "udyamDoc";
        };

        PromptResponse response =
                promptService.latest(WORKFLOW, kind);

        if (response == null
                || response.content() == null
                || response.content().isBlank()) {
            throw new IllegalStateException(
                    "Prompt is empty or unavailable in lakeFS: " + kind
            );
        }

        String markdown = response.content();
        Matcher matcher = METADATA_BLOCK.matcher(markdown);

        if (!matcher.find()) {
            throw new IllegalStateException(
                    "referenceId metadata is missing from lakeFS prompt: "
                            + kind
            );
        }

        String referenceId = matcher.group(1);

        // Remove metadata before passing the SOP to the LLM.
        String promptContent = matcher.replaceFirst("").trim();

        if (promptContent.isBlank()) {
            throw new IllegalStateException(
                    "SOP content is empty: " + kind
            );
        }

        return new LoadedPrompt(
                referenceId,
                promptContent,
                response.promptId(),
                response.ref()
        );
    }

    public record LoadedPrompt(
            String referenceId,
            String content,
            String promptId,
            String ref
    ) {}
}

//import com.example.nlq.OnboardingStep;
//import com.example.nlq.dto.PromptResponse;
//import org.springframework.stereotype.Service;
//
//@Service
//public class OnboardingPromptService {
//
//    private static final String WORKFLOW = "ddod";
//
//    private final PromptService promptService;
//
//    public OnboardingPromptService(PromptService promptService) {
//        this.promptService = promptService;
//    }
//
//    public String loadPrompt(OnboardingStep step) {
//
//        if (step == null) {
//            throw new IllegalArgumentException(
//                    "Onboarding step is required."
//            );
//        }
//
//        String kind = switch (step) {
//            case OTP_VERIFICATION -> "otp";
//            case PROFILE_DETAILS -> "profileDetails";
//            case ACCOUNT_STATEMENT -> "accountStatement";
//            case UDYAM_DOCUMENT -> "udyamDoc";
//        };
//
//        PromptResponse response =
//                promptService.latest(WORKFLOW, kind);
//
//        if (response == null
//                || response.content() == null
//                || response.content().isBlank()) {
//            throw new IllegalStateException(
//                    "Onboarding prompt is empty or unavailable in lakeFS: "
//                            + kind
//            );
//        }
//
//        return response.content();
//    }
//}
//

//import com.example.nlq.OnboardingStep;
//import io.lakefs.clients.sdk.ApiClient;
//import io.lakefs.clients.sdk.ApiException;
//import io.lakefs.clients.sdk.ObjectsApi;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//
//import java.io.File;
//import java.io.IOException;
//import java.nio.charset.StandardCharsets;
//import java.nio.file.Files;
//
//@Service
//public class OnboardingPromptService {
//
//    private final ApiClient lakeFsApiClient;
//    private final String repository;
//    private final String branch;
//    private final String promptPrefix;
//
//    public OnboardingPromptService(
//            ApiClient lakeFsApiClient,
//            @Value("${lakefs.repository:brain-config}")
//            String repository,
//            @Value("${lakefs.branch:main}")
//            String branch,
//            @Value("${lakefs.prompt-prefix:prompts/onboarding}")
//            String promptPrefix) {
//
//        this.lakeFsApiClient = lakeFsApiClient;
//        this.repository = repository;
//        this.branch = branch;
//        this.promptPrefix = promptPrefix;
//    }
//
//    public String loadPrompt(OnboardingStep step) {
//
//        if (step == null) {
//            throw new IllegalArgumentException(
//                    "Onboarding step is required."
//            );
//        }
//
//        String prefix = promptPrefix
//                .replaceAll("^/+", "")
//                .replaceAll("/+$", "");
//
//        String objectPath =
//                prefix + "/" + step.getPromptFileName();
//
//        File downloadedFile = null;
//
//        try {
//            ObjectsApi objectsApi =
//                    new ObjectsApi(lakeFsApiClient);
//
//            downloadedFile = objectsApi
//                    .getObject(repository, branch, objectPath)
//                    .execute();
//
//            if (downloadedFile == null
//                    || !downloadedFile.isFile()) {
//                throw new IllegalStateException(
//                        "lakeFS returned no file for object: "
//                                + objectPath
//                );
//            }
//
//            String prompt = Files.readString(
//                    downloadedFile.toPath(),
//                    StandardCharsets.UTF_8
//            );
//
//            if (prompt.isBlank()) {
//                throw new IllegalStateException(
//                        "The onboarding prompt is empty: "
//                                + objectPath
//                );
//            }
//
//            return prompt;
//
//        } catch (ApiException e) {
//            throw new IllegalStateException(
//                    "Could not read onboarding prompt from lakeFS. "
//                            + "Repository=" + repository
//                            + ", branch=" + branch
//                            + ", path=" + objectPath
//                            + ", HTTP status=" + e.getCode(),
//                    e
//            );
//        } catch (IOException e) {
//            throw new IllegalStateException(
//                    "Could not read downloaded prompt file: "
//                            + objectPath,
//                    e
//            );
//        } finally {
//            if (downloadedFile != null) {
//                try {
//                    Files.deleteIfExists(downloadedFile.toPath());
//                } catch (IOException ignored) {
//                    // A temporary-file cleanup failure must not hide
//                    // the prompt loading result.
//                }
//            }
//        }
//    }
//}

//public class OnboardingPromptService {
//
//    public String loadPrompt(OnboardingStep step) {
//
//        if (step == null) {
//            throw new IllegalArgumentException(
//                    "Onboarding step is required."
//            );
//        }
//
//        Resource resource = new ClassPathResource(
//                "prompts/onboarding/" + step.getPromptFileName()
//        );
//
//        try (InputStream input = resource.getInputStream()) {
//            return StreamUtils.copyToString(
//                    input,
//                    StandardCharsets.UTF_8
//            );
//        } catch (IOException e) {
//            throw new IllegalStateException(
//                    "Could not load onboarding prompt file: "
//                            + step.getPromptFileName(),
//                    e
//            );
//        }
//    }
//}
