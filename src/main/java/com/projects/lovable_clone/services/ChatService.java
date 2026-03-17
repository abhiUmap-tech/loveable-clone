package com.projects.lovable_clone.services;

import com.projects.lovable_clone.dtos.chat.ChatResponse;
import com.projects.lovable_clone.entity.ChatMessage;

import java.util.List;

public interface ChatService {

    List<ChatResponse> getProjectChatHistory(Long projectId);
}
