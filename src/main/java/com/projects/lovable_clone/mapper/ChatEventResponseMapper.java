package com.projects.lovable_clone.mapper;


import com.projects.lovable_clone.dtos.chat.ChatEventResponse;
import com.projects.lovable_clone.entity.ChatEvent;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ChatEventResponseMapper {

    List<ChatEventResponse> toChatEventResponse(List<ChatEvent> chatEventList);
}
