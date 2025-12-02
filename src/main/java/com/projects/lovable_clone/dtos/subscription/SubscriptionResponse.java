package com.projects.lovable_clone.dtos.subscription;

import java.time.Instant;

public record SubscriptionResponse(
        PlanResponse planResponse,
        String status,
        Instant periodEnd,
        Long tokensUsedThisCycle) {
}
