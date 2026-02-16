package com.projects.lovable_clone.config;

import com.stripe.Stripe;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class PaymentConfig {

    @Value("${stripe.secret}")
    private String stripeSecretKey;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeSecretKey;

        // Safe sanity log (do NOT log full key)
        log.info("Stripe API key loaded successfully");
    }
}
