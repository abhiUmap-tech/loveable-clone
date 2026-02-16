package com.projects.lovable_clone.entity;

import com.projects.lovable_clone.enums.SubscriptionStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
@Table(name = "subscriptions")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserSubscription {  // ← Renamed from Subscription

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    Plan plan;

    @Enumerated(EnumType.STRING)
    @Column(name = "subscription_status", nullable = false, length = 50)
    SubscriptionStatus subscriptionStatus;

    @Column(name = "stripe_subscription_id", unique = true, length = 255)
    String stripeSubscriptionId;

    @Column(name = "current_period_start")
    Instant currentPeriodStart;

    @Column(name = "current_period_end")
    Instant currentPeriodEnd;


    @Column(name = "cancel_at_period_end", nullable = false)
    Boolean cancelAtPeriodEnd = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    Instant createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    Instant updatedAt;



}