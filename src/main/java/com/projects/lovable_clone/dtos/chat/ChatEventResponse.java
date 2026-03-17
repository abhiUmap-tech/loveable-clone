package com.projects.lovable_clone.dtos.chat;

import com.projects.lovable_clone.entity.ChatMessage;
import com.projects.lovable_clone.enums.ChatEventType;

public record ChatEventResponse(
        Long id,
        ChatEventType chatEventType,
        Integer sequenceOrder,
        String content,
        String filePath,
        String metadata) {
}
