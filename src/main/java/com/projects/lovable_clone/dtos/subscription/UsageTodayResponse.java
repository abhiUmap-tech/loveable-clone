package com.projects.lovable_clone.dtos.subscription;

public record UsageTodayResponse(
        int tokenUsed,
        int tokenLimit,
        int previewRunning,
        int previewLimit
) {
}
