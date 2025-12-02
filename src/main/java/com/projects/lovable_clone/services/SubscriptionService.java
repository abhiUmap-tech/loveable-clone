package com.projects.lovable_clone.services;

import com.projects.lovable_clone.dtos.subscription.CheckoutRequest;
import com.projects.lovable_clone.dtos.subscription.CheckoutResponse;
import com.projects.lovable_clone.dtos.subscription.PortalResponse;
import com.projects.lovable_clone.dtos.subscription.SubscriptionResponse;
import org.jspecify.annotations.Nullable;

public interface SubscriptionService {
    SubscriptionResponse getCurrentSubscription(Long userId);

    CheckoutResponse createCheckoutSessionUrl(CheckoutRequest checkoutRequest, Long userId);

     PortalResponse openCustomerPortal(Long userId);
}
