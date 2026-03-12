package com.projects.lovable_clone.services;

import io.micrometer.observation.ObservationFilter;
import reactor.core.publisher.Flux;

public interface AIGenerationService {
    Flux<String> streamResponse(String userMessage, Long projectId);
}
