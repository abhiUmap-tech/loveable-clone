package com.projects.lovable_clone.repository;

import com.projects.lovable_clone.entity.UserSubscription;  // ← Updated
import com.projects.lovable_clone.enums.SubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface SubscriptionRepository extends JpaRepository<UserSubscription, Long> {  // ← Updated
    /**
     * Get the current active subscription
     */
    Optional<UserSubscription> findByIdAndSubscriptionStatusIn(Long user_id, Set<SubscriptionStatus> subscriptionStatusSet);  // ← Updated

     boolean existsByStripeSubscriptionId(String subscriptionId);


    Optional<UserSubscription> findByStripeSubscriptionId(String gatewaySubscriptionId);
}