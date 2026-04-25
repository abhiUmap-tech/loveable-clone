package com.projects.lovable_clone.controllers.chat;

import com.projects.lovable_clone.dtos.chat.ChatRequest;
import com.projects.lovable_clone.dtos.chat.ChatResponse;
import com.projects.lovable_clone.dtos.chat.StreamResponse;
import com.projects.lovable_clone.services.AIGenerationService;
import com.projects.lovable_clone.services.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat")
public class ChatController {

    private final AIGenerationService aiGenerationService;
    private final ChatService chatService;

    @PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<StreamResponse>> streamChat(@RequestBody ChatRequest request){

        return aiGenerationService.streamResponse(request.userMessage(), request.projectId())
                .map(data -> ServerSentEvent.<StreamResponse>builder()
                        .data(data)
                        .build());
    }

    @GetMapping("/projects/{projectId}")
    public ResponseEntity<List<ChatResponse>> getChatHistory(@PathVariable Long projectId){
        return ResponseEntity.ok(chatService.getProjectChatHistory(projectId));
    }


}
