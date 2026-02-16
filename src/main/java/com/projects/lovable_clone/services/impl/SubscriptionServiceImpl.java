package com.projects.lovable_clone.services.impl;

import com.projects.lovable_clone.dtos.subscription.SubscriptionResponse;
import com.projects.lovable_clone.entity.Plan;
import com.projects.lovable_clone.entity.User;
import com.projects.lovable_clone.entity.UserSubscription;
import com.projects.lovable_clone.enums.SubscriptionStatus;
import com.projects.lovable_clone.error.ResourceNotFoundException;
import com.projects.lovable_clone.mapper.SubscriptionMapper;
import com.projects.lovable_clone.repository.PlanRepository;
import com.projects.lovable_clone.repository.SubscriptionRepository;
import com.projects.lovable_clone.repository.UserRepository;
import com.projects.lovable_clone.security.AuthUtil;
import com.projects.lovable_clone.services.SubscriptionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Set;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class SubscriptionServiceImpl implements SubscriptionService {

    AuthUtil authUtil;
    SubscriptionRepository subscriptionRepository;
    SubscriptionMapper subscriptionMapper;
    UserRepository userRepository;
    PlanRepository planRepository;


    @Override
    public SubscriptionResponse getCurrentSubscription() {
        var userId = authUtil.getCurrentUserId();

       var currentSubscription = subscriptionRepository.findByIdAndSubscriptionStatusIn(userId, Set.of(
                SubscriptionStatus.ACTIVE, SubscriptionStatus.PAST_DUE,
                SubscriptionStatus.TRAILING))
               .orElse((new UserSubscription()));

       return subscriptionMapper.toSubscriptionResponse(currentSubscription);
    }

    @Override
    public void activateSubscription(Long userId, Long planId, String subscriptionId, String customerId) {
        var exists = subscriptionRepository.existsByStripeSubscriptionId(subscriptionId);
        if (exists) return;

        var user = getUserById(userId);
        var plan = getPlan(planId);

        var subscription = UserSubscription.builder()
                .plan(plan)
                .user(user)
                .stripeSubscriptionId(subscriptionId)
                .subscriptionStatus(SubscriptionStatus.INCOMPLETE)
                .build();

        subscriptionRepository.save(subscription);
    }

    @Override
    public void updateSubscription(String subscriptionId, SubscriptionStatus status, Instant periodStart, Instant periodEnd, Boolean cancelAtPeriodEnd, Long planId) {

    }

    @Override
    public void cancelSubscription(String subscriptionId) {

    }

    @Override
    public void renewSubscriptionPeriod(String gatewaySubscriptionId, Instant periodStart, Instant periodEnd) {
        var subscription = getSubscription(gatewaySubscriptionId);

        var newStart = periodStart != null ? periodEnd : subscription.getCurrentPeriodEnd();
        subscription.setCurrentPeriodEnd(newStart);
        subscription.setCurrentPeriodEnd(periodEnd);

        if (subscription.getSubscriptionStatus() == SubscriptionStatus.PAST_DUE || subscription.getSubscriptionStatus() == SubscriptionStatus.INCOMPLETE)
            subscription.setSubscriptionStatus(SubscriptionStatus.ACTIVE);

        subscriptionRepository.save(subscription);
    }



    @Override
    public void markSubscriptionPastDue(String subscriptionId) {

    }

    //Utility Methods

    private User getUserById(Long userId){
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId.toString()));
    }

    private Plan getPlan(Long planId){
        return planRepository.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("Plan", planId.toString()));
    }

    private UserSubscription getSubscription(String gatewaySubscriptionId) {
        return subscriptionRepository.findByStripeSubscriptionId(gatewaySubscriptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription", gatewaySubscriptionId));
    }

}
