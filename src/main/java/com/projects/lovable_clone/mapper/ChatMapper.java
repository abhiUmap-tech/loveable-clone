package com.projects.lovable_clone.mapper;

import com.projects.lovable_clone.dtos.chat.ChatResponse;
import com.projects.lovable_clone.entity.ChatMessage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ChatMapper {

    @Mapping(target = "projectId", source = "chatSession.id.projectId")
    @Mapping(target = "userId", source = "chatSession.id.userId")
    @Mapping(target = "tokenUsed", source = "tokensUsed")
    ChatResponse fromChatMessage(ChatMessage chatMessage);

    List<ChatResponse> fromListOfChatMessage(List<ChatMessage> chatMessageList);
}
