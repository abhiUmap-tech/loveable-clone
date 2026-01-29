package com.projects.lovable_clone.services;

import com.projects.lovable_clone.dtos.subscription.PlanLimitResponse;
import com.projects.lovable_clone.dtos.subscription.UsageTodayResponse;
import org.jspecify.annotations.Nullable;

public interface UsageService {

     UsageTodayResponse getTodayUsageUser(Long userId);

    PlanLimitResponse getCurrentSubscriptionLimitsOfUser(Long userId);
}
