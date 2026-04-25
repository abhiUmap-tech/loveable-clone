package com.projects.lovable_clone.services.impl;

import com.projects.lovable_clone.dtos.chat.StreamResponse;
import com.projects.lovable_clone.entity.*;
import com.projects.lovable_clone.enums.ChatEventType;
import com.projects.lovable_clone.enums.MessageRole;
import com.projects.lovable_clone.error.ResourceNotFoundException;
import com.projects.lovable_clone.llm.LLMResponseParser;
import com.projects.lovable_clone.llm.PromptUtils;
import com.projects.lovable_clone.llm.advisors.FileTreeContextAdvisor;
import com.projects.lovable_clone.llm.tools.CodeGenerationTools;
import com.projects.lovable_clone.repository.*;
import com.projects.lovable_clone.security.AuthUtil;
import com.projects.lovable_clone.services.AIGenerationService;
import com.projects.lovable_clone.services.ProjectFileService;
import com.projects.lovable_clone.services.UsageService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;

@Service
@Slf4j
@RequiredArgsConstructor
public class AIGenerationServiceImpl implements AIGenerationService {

    private final ChatClient chatClient;
    private final AuthUtil authUtil;
    private final ProjectFileService projectFileService;
    private final FileTreeContextAdvisor fileTreeContextAdvisor;
    private final LLMResponseParser llmResponseParser;
    private final ChatSessionRepository chatSessionRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatEventRepository chatEventRepository;
    private final UsageService usageService;

    private static final Pattern FILE_TAG_PATTERN = Pattern.compile("<file path=\"([^\"]+)\">(.*?)</file>", Pattern.DOTALL);


    @Override
    @PreAuthorize("@security.canEditProject(#projectId)")
    public Flux<StreamResponse> streamResponse(String userMessage, Long projectId) {

//        usageService.checkDailyTokensUsage();

        log.info("streamResponse called - userMessage: {}, projectId: {}", userMessage, projectId);
        var userId = authUtil.getCurrentUserId();
        var chatSession = createChatSessionIfNotExists(projectId, userId);

        Map<String, Object> advisorParams = Map.of(
                "userId", userId,
                "projectId", projectId);

        var fullResponseBuffer = new StringBuilder();
        var codeGenerationTools = new CodeGenerationTools(projectFileService, projectId);

        var startTime = new AtomicReference<>(System.currentTimeMillis());
        var endTime = new AtomicReference<>(0L);
        AtomicReference<Usage> usageRef = new AtomicReference<>();

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

                    if(content != null && !content.isEmpty() && endTime.get() == 0)
                        endTime.set(System.currentTimeMillis());

                    if(chatResponse.getMetadata().getUsage() != null) {
                        usageRef.set(chatResponse.getMetadata().getUsage());
                    }

                    fullResponseBuffer.append(content);
                })
                .doOnComplete(() -> Schedulers.boundedElastic().schedule(() -> {
                   // parseAndSaveFiles(fullResponseBuffer.toString(), projectId);

                    long duration = (endTime.get() - startTime.get()) / 1000;
                    finalizeChats(userMessage, chatSession, fullResponseBuffer.toString(), duration, usageRef.get(), userId);
                }))
                .doOnError(error -> log.error("Error during streaming for the projectId: {}", projectId))
                .map(response -> {
                    String text = response.getResult().getOutput().getText();
                    return new StreamResponse(text != null ? text : "");
                });
    }

    private void finalizeChats(String userMessage, ChatSession chatSession, String fullText, Long duration, Usage usage, Long userId) {
        var projectId = chatSession.getProject().getId();

        if (usage != null){
            var totalTokens = Objects.requireNonNull(usage).getTotalTokens();
            usageService.recordTokenUsage(userId, totalTokens);
        }

        // Save the user message
        chatMessageRepository.save(
                ChatMessage.builder()
                        .chatSession(chatSession)
                        .messageRole(MessageRole.USER)
                        .content(userMessage)
                        .tokensUsed(usage.getPromptTokens())
                        .build());

        // Build assistant message with content BEFORE saving
        ChatMessage assistantChatMessage = ChatMessage.builder()
                .messageRole(MessageRole.ASSISTANT)
                .chatSession(chatSession)
                .content(fullText)  // ← set content here, not after
                .tokensUsed(usage.getCompletionTokens())
                .build();

        assistantChatMessage = chatMessageRepository.save(assistantChatMessage);

        var chatEventList = llmResponseParser.parseChatEvents(fullText, assistantChatMessage);
        chatEventList.addFirst(ChatEvent.builder()
                        .type(ChatEventType.THOUGHT)
                        .chatMessage(assistantChatMessage)
                        .content("Thought for "+ duration +"s")
                        .sequenceOrder(0)
                .build());

        chatEventList.stream()
                .filter(chatEvent -> chatEvent.getType() == ChatEventType.FILE_EDIT)
                .forEach(chatEvent -> projectFileService.saveFile(projectId, chatEvent.getFilePath(), chatEvent.getContent()));

        chatEventRepository.saveAll(chatEventList);
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

    private ChatSession createChatSessionIfNotExists(Long projectId, Long userId) {
        ChatSessionId chatSessionId = new ChatSessionId(projectId, userId);
        var chatSession = chatSessionRepository.findById(chatSessionId)
                .orElse(null);

        if (chatSession == null) {
            Project project = projectRepository.findById(projectId)
                    .orElseThrow(() -> new ResourceNotFoundException("project", projectId.toString()));

            var user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User", userId.toString()));

            chatSession = ChatSession.builder()
                    .id(chatSessionId)
                    .project(project)
                    .user(user)
                    .build();

            chatSession = chatSessionRepository.save(chatSession);
        }

        return chatSession;
    }
}
