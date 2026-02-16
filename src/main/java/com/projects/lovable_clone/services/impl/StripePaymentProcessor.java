package com.projects.lovable_clone.services.impl;

import com.projects.lovable_clone.dtos.subscription.CheckoutRequest;
import com.projects.lovable_clone.dtos.subscription.CheckoutResponse;
import com.projects.lovable_clone.dtos.subscription.PortalResponse;
import com.projects.lovable_clone.entity.Plan;
import com.projects.lovable_clone.entity.User;
import com.projects.lovable_clone.entity.UserSubscription;
import com.projects.lovable_clone.enums.SubscriptionStatus;
import com.projects.lovable_clone.error.ResourceNotFoundException;
import com.projects.lovable_clone.repository.PlanRepository;
import com.projects.lovable_clone.repository.SubscriptionRepository;
import com.projects.lovable_clone.repository.UserRepository;
import com.projects.lovable_clone.security.AuthUtil;
import com.projects.lovable_clone.services.PaymentProcessor;
import com.projects.lovable_clone.services.SubscriptionService;
import com.stripe.exception.StripeException;
import com.stripe.model.Invoice;
import com.stripe.model.Price;
import com.stripe.model.StripeObject;
import com.stripe.model.Subscription;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.time.Instant;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StripePaymentProcessor implements PaymentProcessor {

    AuthUtil authUtil;
    PlanRepository planRepository;

    String frontendUrl;
    UserRepository userRepository;

    SubscriptionService subscriptionService;
    SubscriptionRepository subscriptionRepository;



    public StripePaymentProcessor(
            AuthUtil authUtil,
            PlanRepository planRepository,
            @Value("${client.url}") String frontendUrl, UserRepository userRepository, SubscriptionService subscriptionService, SubscriptionRepository subscriptionRepository) {
        this.authUtil = authUtil;
        this.planRepository = planRepository;
        this.frontendUrl = frontendUrl;
        this.userRepository = userRepository;
        this.subscriptionService = subscriptionService;

        this.subscriptionRepository = subscriptionRepository;
    }

    @Override
    public CheckoutResponse createCheckoutSessionUrl(CheckoutRequest checkoutRequest) throws StripeException {
        log.debug("Creating checkout session for plan ID: {}", checkoutRequest.planId());

        var plan = planRepository.findById(checkoutRequest.planId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Plan", checkoutRequest.planId().toString()));

        // Validate plan configuration
        if (plan.getStripePriceId() == null || plan.getStripePriceId().isBlank()) {
            log.error("Plan {} does not have a valid Stripe price ID", plan.getId());
            throw new IllegalStateException("Plan is not properly configured for payments");
        }

        var userId = authUtil.getCurrentUserId();
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId.toString()));

        var params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                .addLineItem(SessionCreateParams.LineItem.builder()
                        .setPrice(plan.getStripePriceId())
                        .setQuantity(1L)
                        .build())
                .setSubscriptionData(SessionCreateParams.SubscriptionData.builder()
                        .putMetadata("userId", userId.toString())
                        .putMetadata("planId", plan.getId().toString())
                        .build())
                .setSuccessUrl(frontendUrl + "/success?session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl(frontendUrl + "/cancel");

        var stripeCustomerId = user.getStripeCustomerId();
        if (stripeCustomerId == null || stripeCustomerId.isEmpty()) {
            params.setCustomerEmail(user.getUsername());
            log.debug("Using email for new customer: {}", user.getUsername());
        } else {
            params.setCustomer(stripeCustomerId);
            log.debug("Using existing Stripe customer: {}", stripeCustomerId);
        }

        try {
            Session session = Session.create(params.build());
            log.info("Created checkout session {} for user {} and plan {}",
                    session.getId(), userId, plan.getId());
            return new CheckoutResponse(session.getUrl());
        } catch (StripeException e) {
            log.error("Failed to create checkout session for user {} and plan {}: {}",
                    userId, plan.getId(), e.getMessage(), e);
            throw e;
        }
    }



    @Override
    public PortalResponse openCustomerPortal() {
        return null;
    }

    @Override
    public void handleWebhookEvent(String type, StripeObject stripeObject, Map<String, String> metaData) {
        log.info("Processing webhook event: {}",type);

        switch (type) {
            case "checkout.session.completed" -> handleCheckoutSessionCompleted((Session) stripeObject, metaData);
            case "customer.subscription.updated" -> handleSubscriptionUpdated((Subscription) stripeObject);
            case "customer.subscription.deleted" -> handleSubscriptionDeleted((Subscription) stripeObject);
            case "invoice.paid" -> handleInvoicePaid((Invoice) stripeObject);
            case "invoice.payment_failed" -> handleInvoicePaymentFailed((Invoice) stripeObject);
            default -> log.debug("Unhandled event type: {}", type);
        }


    }

    private void handleCheckoutSessionCompleted(Session session, Map<String, String> metadata) {
        if (session == null){
            log.error("session object is null inside handleCheckoutSessionCompleted");
            return;
        }
        log.info("📝 Processing checkout.session.completed");

        var userId = Long.parseLong(metadata.get("user_id"));
        var planId = Long.parseLong(metadata.get("plan_id"));

        var subscriptionId = session.getSubscription();
        var customerId = session.getCustomer();

        var user = getUser(userId);

        if (user.getStripeCustomerId() == null){
            user.setStripeCustomerId(customerId);
            userRepository.save(user);
        }
        subscriptionService.activateSubscription(userId, planId, subscriptionId, customerId);
    }



    private void handleSubscriptionUpdated(Subscription subscription) {
        if (subscription == null){
            log.error("Subscription object was null inside handleSubscriptionUpdated");
            return;
        }

        var status = mapStripeStatusToEnum(subscription.getStatus());

        if (status == null){
            log.warn("Unknown status '{}' for subscription {}", subscription.getStatus(), subscription.getId());
            return;
        }

        var item = subscription.getItems().getData().getFirst();

        var periodStart = toInstant(item.getCurrentPeriodEnd());
        var periodEnd = toInstant(item.getCurrentPeriodEnd());

        var planId = resolvePlanId(item.getPrice());

        subscriptionService.updateSubscription(
                subscription.getId(), status, periodStart, periodEnd,
                subscription.getCancelAtPeriodEnd(), planId);
    }



    private void handleSubscriptionDeleted(Subscription subscription) {
        log.info("❌ Processing customer.subscription.deleted");
        // Mark subscription as canceled in your database
        if(subscription == null){
            log.error("subscription object was null");
            return;
        }
        subscriptionService.cancelSubscription(subscription.getId());
    }

    private void handleInvoicePaid(Invoice invoice) {
        log.info("💰 Processing invoice.payment_succeeded");
        // Update payment status, extend subscription period
        var subscriptionId = extractSubscriptionId(invoice);

        if (subscriptionId == null) return;

        try {
            var subscription = Subscription.retrieve(subscriptionId); //sdk calling the Stripe server
            var item = subscription.getItems().getData().getFirst();

            var periodStart = toInstant(item.getCurrentPeriodStart());
            var periodEnd = toInstant(item.getCurrentPeriodEnd());

            subscriptionService.renewSubscriptionPeriod(
                    subscriptionId, periodStart, periodEnd);


        } catch (StripeException e) {
            throw new RuntimeException(e);
        }

    }

    private void handleInvoicePaymentFailed(Invoice invoice) {
        log.warn("⚠️ Processing invoice.payment_failed");
        // Notify user, handle failed payment
        var subscriptionId = extractSubscriptionId(invoice);
        if (subscriptionId == null) return;

        subscriptionService.markSubscriptionPastDue(subscriptionId);
    }


    // Utility Methods

    private User getUser(Long userId){
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId.toString()));

    }

    private SubscriptionStatus mapStripeStatusToEnum(String status) {
       return switch (status) {
           case "active" -> SubscriptionStatus.ACTIVE;
           case "trailing" -> SubscriptionStatus.TRAILING;
           case "past_due", "unpaid", "paused", "incompleted_expired" -> SubscriptionStatus.PAST_DUE;
           case "canceled" -> SubscriptionStatus.CANCELLED;
           case "incompleted" -> SubscriptionStatus.INCOMPLETE;

           default -> {
               log.warn("Unmapped Stripe status: {}", status);
               yield null;
           }
       };
    }

    private Instant toInstant(Long epoch) {
        return epoch != null ? Instant.ofEpochSecond(epoch) : null;
    }

    private Long resolvePlanId(Price price) {
        return planRepository.findByStripePriceId(price.toString())
                .map(Plan::getId)
                .orElse(null);
    }

    private String extractSubscriptionId(Invoice invoice){
        var parent = invoice.getParent();
        if (parent == null) return null;

        var subDetails = parent.getSubscriptionDetails();
        if (subDetails == null) return null;

        return subDetails.getSubscription();
    }


}
