package com.projects.lovable_clone.services;

import com.projects.lovable_clone.dtos.subscription.CheckoutRequest;
import com.projects.lovable_clone.dtos.subscription.CheckoutResponse;
import com.projects.lovable_clone.dtos.subscription.PortalResponse;
import com.stripe.exception.StripeException;
import com.stripe.model.StripeObject;

import java.util.Map;

public interface PaymentProcessor {

    CheckoutResponse createCheckoutSessionUrl(CheckoutRequest checkoutRequest) throws StripeException;

    PortalResponse openCustomerPortal();

    void handleWebhookEvent(String type, StripeObject stripeObject, Map<String, String> metaData);
}
