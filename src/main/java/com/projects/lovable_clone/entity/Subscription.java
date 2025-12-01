package com.projects.lovable_clone.entity;

import com.projects.lovable_clone.enums.SubscriptionStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Subscription {

    Long id;

    User user;

    Plan plan;

    SubscriptionStatus subscriptionStatus;

    String stripeCustomerId;
    String stripeSubscriptionId;

    Instant currentPeriodStart;
    Instant currentPeriodEnd;
    Instant createdAt;
    Instant updatedAt;

    Boolean cancelAtPeriodEnd = false;


}
