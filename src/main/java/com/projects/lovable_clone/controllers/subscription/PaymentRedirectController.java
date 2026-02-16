package com.projects.lovable_clone.controllers.subscription;


import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@Slf4j
public class PaymentRedirectController {

    /**
     * Success page after Stripe checkout
     * User is redirected here after successful payment
     */
    @GetMapping("/success")
    public String paymentSuccess(@RequestParam("session_id") String sessionId, Model model) {
        log.info("Payment success redirect - Session ID: {}", sessionId);

        try {
            // Retrieve the session to get details
            Session session = Session.retrieve(sessionId);

            model.addAttribute("sessionId", sessionId);
            model.addAttribute("customerEmail", session.getCustomerDetails().getEmail());
            model.addAttribute("amountTotal", session.getAmountTotal() / 100.0); // Convert from cents

            log.info("Payment successful for session: {}", sessionId);

            // Return view name (create payment-success.html in templates folder)
            return "payment-success";

        } catch (StripeException e) {
            log.error("Error retrieving session {}: {}", sessionId, e.getMessage());
            model.addAttribute("error", "Could not verify payment");
            return "payment-error";
        }
    }

    /**
     * Cancel page when user cancels checkout
     */
    @GetMapping("/cancel")
    public String paymentCancel(Model model) {
        log.info("Payment cancelled by user");

        model.addAttribute("message", "Payment was cancelled. You can try again anytime.");

        // Return view name (create payment-cancel.html in templates folder)
        return "payment-cancel";
    }
}