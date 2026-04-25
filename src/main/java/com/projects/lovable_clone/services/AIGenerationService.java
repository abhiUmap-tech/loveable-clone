package com.projects.lovable_clone.services;

import com.projects.lovable_clone.dtos.chat.StreamResponse;
import io.micrometer.observation.ObservationFilter;
import reactor.core.publisher.Flux;

public interface AIGenerationService {
    Flux<StreamResponse> streamResponse(String userMessage, Long projectId);
}
