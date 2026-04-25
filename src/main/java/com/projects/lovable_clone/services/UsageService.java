package com.projects.lovable_clone.services;

import com.projects.lovable_clone.dtos.subscription.PlanLimitResponse;
import com.projects.lovable_clone.dtos.subscription.UsageTodayResponse;
import org.jspecify.annotations.Nullable;

public interface UsageService {
    void recordTokenUsage(Long userId, int actualToken);
    void checkDailyTokensUsage();
}
