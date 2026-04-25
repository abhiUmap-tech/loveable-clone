package com.projects.lovable_clone.services.impl;

import com.projects.lovable_clone.dtos.subscription.PlanLimitResponse;
import com.projects.lovable_clone.dtos.subscription.UsageTodayResponse;
import com.projects.lovable_clone.entity.UsageLog;
import com.projects.lovable_clone.repository.UsageLogRepository;
import com.projects.lovable_clone.security.AuthUtil;
import com.projects.lovable_clone.services.SubscriptionService;
import com.projects.lovable_clone.services.UsageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class UsageServiceImpl implements UsageService {

    private final UsageLogRepository usageLogRepository;
    private final AuthUtil authUtil;
    private final SubscriptionService subscriptionService;

    @Override
    public void recordTokenUsage(Long userId, int actualToken) {
        var today = LocalDate.now();

        var todayLog = usageLogRepository.findByUserIdAndDate(userId, today)
                .orElseGet(() -> createNewDailyLog(userId, today));

        todayLog.setTokensUsed(todayLog.getTokensUsed() + actualToken);
        usageLogRepository.save(todayLog);

    }

    @Override
    public void checkDailyTokensUsage() {
        var userId = authUtil.getCurrentUserId();
        var subscriptionResponse = subscriptionService.getCurrentSubscription();
        var plan = subscriptionResponse.planResponse();

        var today = LocalDate.now();

        var todayLog = usageLogRepository.findByUserIdAndDate(userId, today)
                .orElseGet(() -> createNewDailyLog(userId, today));

        if (plan.unlimitedAI()) return;

        int currentUsage = todayLog.getTokensUsed();
        int limit = plan.maxTokensPerDay();

        if (currentUsage >= limit)
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS,
                    "Daily limit exceeded, Upgrade now");

    }

    private UsageLog createNewDailyLog(Long userId, LocalDate date) {
        var newLog = UsageLog.builder()
                .userId(userId)
                .date(date)
                .tokensUsed(0)
                .build();

        return usageLogRepository.save(newLog);
    }
}
