package com.projects.lovable_clone.llm.advisors;

import com.projects.lovable_clone.services.ProjectFileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisor;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisorChain;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class FileTreeContextAdvisor implements StreamAdvisor {

    private final ProjectFileService projectFileService;

    @Override
    public Flux<ChatClientResponse> adviseStream(ChatClientRequest chatClientRequest, StreamAdvisorChain streamAdvisorChain) {
        var context = chatClientRequest.context();
        var projectId = Long.parseLong(Objects.requireNonNull(context.getOrDefault("projectId", 0)).toString());

        var augmentChatClientRequest = augmentRequestWithFileTree(chatClientRequest, projectId);
        return streamAdvisorChain.nextStream(augmentChatClientRequest);
    }

    private ChatClientRequest augmentRequestWithFileTree(ChatClientRequest request, Long projectId) {

        var incomingMessages = request.prompt().getInstructions();

        // Grab existing system message if present
        Message existingSystemMessage = incomingMessages.stream()
                .filter(message -> message.getMessageType() == MessageType.SYSTEM)
                .findFirst()
                .orElse(null);

        List<Message> allMessages = new ArrayList<>();

        // Build enriched system message (merge file tree into it)
        var fileTree = projectFileService.getFileTree(projectId);
        String fileTreeContext = "\n\n  ----- FILE_TREE ------\n" + fileTree.toString();

        if (existingSystemMessage != null) {
            // Append file tree to existing system message content
            allMessages.add(new SystemMessage(existingSystemMessage.getText() + fileTreeContext));
        } else {
            allMessages.add(new SystemMessage(fileTreeContext));
        }

        // Add all non-system messages (user, assistant, etc.)
        incomingMessages.stream()
                .filter(message -> message.getMessageType() != MessageType.SYSTEM)
                .forEach(allMessages::add);

        return request
                .mutate()
                .prompt(new Prompt(allMessages, request.prompt().getOptions()))
                .build();
    }

    @Override
    public String getName() {
        return "FileTreeContextAdvisor";
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
