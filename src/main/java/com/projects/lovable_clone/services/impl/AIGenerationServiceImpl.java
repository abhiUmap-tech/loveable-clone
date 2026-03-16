package com.projects.lovable_clone.services.impl;

import com.projects.lovable_clone.llm.PromptUtils;
import com.projects.lovable_clone.llm.advisors.FileTreeContextAdvisor;
import com.projects.lovable_clone.llm.tools.CodeGenerationTools;
import com.projects.lovable_clone.security.AuthUtil;
import com.projects.lovable_clone.services.AIGenerationService;
import com.projects.lovable_clone.services.ProjectFileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

@Service
@Slf4j
@RequiredArgsConstructor
public class AIGenerationServiceImpl implements AIGenerationService {

    private final ChatClient chatClient;
    private final AuthUtil authUtil;
    private final ProjectFileService projectFileService;
    private final FileTreeContextAdvisor fileTreeContextAdvisor;

    private static final Pattern FILE_TAG_PATTERN = Pattern.compile("<file path=\"([^\"]+)\">(.*?)</file>", Pattern.DOTALL);


    @Override
    @PreAuthorize("@security.canEditProject(#projectId)")
    public Flux<String> streamResponse(String userMessage, Long projectId) {
        log.info("streamResponse called - userMessage: {}, projectId: {}", userMessage, projectId);
        var userId = authUtil.getCurrentUserId();
        createChatSessionIfNotExists(projectId, userId);

        Map<String, Object> advisorParams = Map.of(
                "userId", userId,
                "projectId", projectId);

        StringBuilder fullResponseBuffer = new StringBuilder();

        var codeGenerationTools = new CodeGenerationTools(projectFileService, projectId);

        return chatClient.prompt()
                .system(PromptUtils.CODE_GENERATION_SYSTEM_PROMPT)
                .user(userMessage)
                .tools(codeGenerationTools)
                .advisors(advisorSpec -> {
                    advisorSpec.params(advisorParams);
                    advisorSpec.advisors(fileTreeContextAdvisor);
                })

                .stream()
                .chatResponse()
                .doOnNext(chatResponse -> {
                    var content = Objects.requireNonNull(chatResponse.getResult()).getOutput().getText();
                    fullResponseBuffer.append(content);
                })
                .doOnComplete(() -> {
                    Schedulers.boundedElastic().schedule(() -> {
                        parseAndSaveFiles(fullResponseBuffer.toString(), projectId);
                    });


                })
                .doOnError(error -> log.error("Error during streaming for the projectId: {}", projectId))
                .map(response -> Objects.requireNonNull(Objects.requireNonNull(response.getResult()).getOutput().getText()));
    }


    private void parseAndSaveFiles(String fullResponse, Long projectId) {
        log.info("Full response:\n{}", fullResponse);
        log.info("Full response length: {}", fullResponse.length());

        var matcher = FILE_TAG_PATTERN.matcher(fullResponse);
        int count = 0;
        while (matcher.find()) {
            count++;
            log.info("Found file #{}: {}", count, matcher.group(1));
            projectFileService.saveFile(projectId, matcher.group(1), matcher.group(2).trim());
        }
        log.info("Total files parsed: {}", count);
    }

    private void createChatSessionIfNotExists(Long projectId, Long userId) {

    }
}
