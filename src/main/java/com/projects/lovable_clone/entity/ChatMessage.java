package com.projects.lovable_clone.entity;

import com.projects.lovable_clone.enums.MessageRole;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChatMessage {

    Long id;

    ChatSession chatSession;

    String content;
    String toolCalls; //JSON array of Tools called

    Integer tokensUsed;

    Instant createdAt;

    MessageRole messageRole;
}
