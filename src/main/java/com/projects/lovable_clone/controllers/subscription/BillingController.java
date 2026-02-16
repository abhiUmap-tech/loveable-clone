package com.projects.lovable_clone.controllers.subscription;

import com.projects.lovable_clone.dtos.subscription.*;
import com.projects.lovable_clone.services.PaymentProcessor;
import com.projects.lovable_clone.services.PlanService;
import com.projects.lovable_clone.services.SubscriptionService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.net.Webhook;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BillingController {

    final PlanService planService;
    final SubscriptionService subscriptionService;
    final PaymentProcessor paymentProcessor;


    @Value("${stripe.webhook.secret}")
    String webhookSecret;

    @GetMapping("/api/plans")
    public ResponseEntity<List<PlanResponse>> getAllPlan(){
        return ResponseEntity.ok(planService.getAllActivePlans());
    }

    @GetMapping("/api/me/subscription")
    public ResponseEntity<SubscriptionResponse> getMySubscription(){
        return ResponseEntity.ok(subscriptionService.getCurrentSubscription());
    }

    @PostMapping("/api/payments/checkout")
    public ResponseEntity<CheckoutResponse> createCheckoutResponse(
            @RequestBody CheckoutRequest checkoutRequest) throws StripeException {
        return ResponseEntity.ok(paymentProcessor.createCheckoutSessionUrl(checkoutRequest));

    }

    @PostMapping("/api/payments/portal")
    public ResponseEntity<PortalResponse> openCustomerPortal(){
        return ResponseEntity.ok(paymentProcessor.openCustomerPortal());
    }

    @PostMapping("/api/stripe/webhook")
    public ResponseEntity<String> handleStripeWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {

        Event event;

        // Verify webhook signature
        try {
            event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
        } catch (SignatureVerificationException e) {
            log.error("⚠️ Webhook signature verification failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid signature");
        } catch (Exception e) {
            log.error("⚠️ Webhook error while constructing event: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Webhook error");
        }

        log.info("✅ Received webhook event: {} [{}]", event.getType(), event.getId());

        // Process the event
        try {
            paymentProcessor.handleWebhookEvent(type, stripeObject, metaData);
        } catch (Exception e) {
            log.error("Error processing webhook event {}: {}", event.getType(), e.getMessage(), e);
            // Still return 200 to acknowledge receipt
        }

        return ResponseEntity.ok("Success");
    }

}
