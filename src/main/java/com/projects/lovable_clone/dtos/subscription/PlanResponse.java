package com.projects.lovable_clone.dtos.subscription;

public record PlanResponse(
        Long id,

        String name,
       String price,

        Integer maxProjects,
        Integer maxTokensPerDay,

        Boolean unlimitedAI) { }
