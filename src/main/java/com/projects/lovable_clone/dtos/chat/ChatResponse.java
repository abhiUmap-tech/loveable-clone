package com.projects.lovable_clone.dtos.chat;

import com.projects.lovable_clone.entity.ChatEvent;
import com.projects.lovable_clone.entity.ChatSession;
import com.projects.lovable_clone.enums.MessageRole;

import java.time.Instant;
import java.util.List;

public record ChatResponse(
        Long id,
        ChatSession chatSession,
        MessageRole messageRole,
        List<ChatEvent> events,
        String content,
        Integer tokenUsed,
        Instant createdAt
) {
}
