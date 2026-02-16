package com.projects.lovable_clone.repository;

import com.projects.lovable_clone.entity.Plan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlanRepository extends JpaRepository<Plan, Long> {

    /**
     * Find plan by Stripe price ID (used in webhook for plan changes)
     */
    Optional<Plan> findByStripePriceId(String stripePriceId);
}
