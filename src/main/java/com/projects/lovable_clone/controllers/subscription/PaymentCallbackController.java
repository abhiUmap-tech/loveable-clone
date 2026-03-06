package com.projects.lovable_clone.controllers.subscription;

import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * REST API version - Use this if your frontend is React/Vue/Angular
 * If you're using server-side templates, use PaymentRedirectController instead
 */
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentCallbackController {

    /**
     * Verify payment success
     * Frontend calls this after being redirected from Stripe
     */
    @GetMapping("/verify")
    public ResponseEntity<Map<String, Object>> verifyPayment(
            @RequestParam("session_id") String sessionId) {

        log.info("Verifying payment - Session ID: {}", sessionId);

        Map<String, Object> response = new HashMap<>();

        try {
            // Retrieve the session to verify payment
            Session session = Session.retrieve(sessionId);

            if ("paid".equals(session.getPaymentStatus())) {
                response.put("success", true);
                response.put("sessionId", sessionId);
                response.put("customerEmail", session.getCustomerDetails().getEmail());
                response.put("amountTotal", session.getAmountTotal() / 100.0);
                response.put("subscriptionId", session.getSubscription());

                log.info("Payment verified successfully for session: {}", sessionId);
            } else {
                response.put("success", false);
                response.put("message", "Payment not completed");
                response.put("paymentStatus", session.getPaymentStatus());

                log.warn("Payment not completed for session: {} - Status: {}",
                        sessionId, session.getPaymentStatus());
            }
            return ResponseEntity.ok(response);

        } catch (StripeException e) {
            log.error("Error verifying session {}: {}", sessionId, e.getMessage());

            response.put("success", false);
            response.put("error", "Could not verify payment");
            response.put("message", e.getMessage());

            return ResponseEntity.status(500).body(response);
        }
    }
}