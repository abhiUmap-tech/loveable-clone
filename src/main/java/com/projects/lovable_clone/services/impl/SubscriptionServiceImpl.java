package com.projects.lovable_clone.services.impl;

import com.projects.lovable_clone.dtos.subscription.CheckoutRequest;
import com.projects.lovable_clone.dtos.subscription.CheckoutResponse;
import com.projects.lovable_clone.dtos.subscription.PortalResponse;
import com.projects.lovable_clone.dtos.subscription.SubscriptionResponse;
import com.projects.lovable_clone.services.SubscriptionService;
import org.springframework.stereotype.Service;

@Service
public class SubscriptionServiceImpl implements SubscriptionService {


    @Override
    public SubscriptionResponse getCurrentSubscription(Long userId) {
        return null;
    }

    @Override
    public CheckoutResponse createCheckoutSessionUrl(CheckoutRequest checkoutRequest, Long userId) {
        return null;
    }

    @Override
    public PortalResponse openCustomerPortal(Long userId) {
        return null;
    }
}
