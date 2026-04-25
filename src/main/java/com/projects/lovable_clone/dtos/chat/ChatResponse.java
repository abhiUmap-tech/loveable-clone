package com.projects.lovable_clone.dtos.chat;

import com.projects.lovable_clone.enums.MessageRole;

import java.time.Instant;
import java.util.List;

public record ChatResponse(
        Long id,
        Long projectId,
        Long userId,
        MessageRole messageRole,
        List<ChatEventResponse> events,
        String content,
        Integer tokenUsed,
        Instant createdAt
) {
}
