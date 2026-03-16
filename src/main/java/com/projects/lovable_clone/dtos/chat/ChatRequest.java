package com.projects.lovable_clone.dtos.chat;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ChatRequest(
        Long projectId,
        @JsonProperty("message") String userMessage
) {}