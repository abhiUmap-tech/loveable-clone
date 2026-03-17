package com.projects.lovable_clone.services.impl;

import com.projects.lovable_clone.dtos.chat.ChatResponse;
import com.projects.lovable_clone.entity.ChatMessage;
import com.projects.lovable_clone.entity.ChatSessionId;
import com.projects.lovable_clone.mapper.ChatMapper;
import com.projects.lovable_clone.repository.ChatMessageRepository;
import com.projects.lovable_clone.repository.ChatSessionRepository;
import com.projects.lovable_clone.security.AuthUtil;
import com.projects.lovable_clone.services.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatSessionRepository chatSessionRepository;
    private final AuthUtil authUtil;
    private final ChatMapper chatMapper;


    @Override
    public List<ChatResponse> getProjectChatHistory(Long projectId) {
        var userId = authUtil.getCurrentUserId();
        var chatSession = chatSessionRepository.getReferenceById(
                new ChatSessionId(projectId, userId));

        var chatMessageList = chatMessageRepository.findByChatSession(chatSession);
        return chatMapper.fromListOfChatMessage(chatMessageList);
    }
}
