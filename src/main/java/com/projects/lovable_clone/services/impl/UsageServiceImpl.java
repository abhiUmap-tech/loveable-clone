package com.projects.lovable_clone.services.impl;

import com.projects.lovable_clone.dtos.subscription.PlanLimitResponse;
import com.projects.lovable_clone.dtos.subscription.UsageTodayResponse;
import com.projects.lovable_clone.services.UsageService;
import org.springframework.stereotype.Service;

@Service
public class UsageServiceImpl implements UsageService {
    @Override
    public UsageTodayResponse getTodayUsageUser(Long userId) {
        return null;
    }

    @Override
    public PlanLimitResponse getCurrentSubscriptionLimitsOfUser(Long userId) {
        return null;
    }
}
