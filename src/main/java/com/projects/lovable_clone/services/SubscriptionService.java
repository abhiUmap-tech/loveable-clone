package com.projects.lovable_clone.services;

import com.projects.lovable_clone.dtos.subscription.CheckoutRequest;
import com.projects.lovable_clone.dtos.subscription.CheckoutResponse;
import com.projects.lovable_clone.dtos.subscription.PortalResponse;
import com.projects.lovable_clone.dtos.subscription.SubscriptionResponse;
import com.projects.lovable_clone.enums.SubscriptionStatus;
import org.jspecify.annotations.Nullable;

import java.time.Instant;

public interface SubscriptionService {
    SubscriptionResponse getCurrentSubscription();

    void activateSubscription(Long userId, Long planId, String subscriptionId, String customerId);

    void updateSubscription(String gatewaySubscriptionId, SubscriptionStatus status, Instant periodStart, Instant periodEnd, Boolean cancelAtPeriodEnd, Long planId);

    void cancelSubscription(String gatewaySubscriptionId);

    void renewSubscriptionPeriod(String gatewaySubscriptionId, Instant periodStart, Instant periodEnd);

    void markSubscriptionPastDue(String subscriptionId);

    boolean canCreateNewProject();
}
