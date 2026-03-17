package com.projects.lovable_clone.mapper;

import com.projects.lovable_clone.dtos.chat.ChatResponse;
import com.projects.lovable_clone.entity.ChatMessage;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ChatMapper {

    List<ChatResponse> fromListOfChatMessage(List<ChatMessage> chatMessageList);
}
