package com.projects.lovable_clone.mapper;

import com.projects.lovable_clone.dtos.subscription.PlanResponse;
import com.projects.lovable_clone.dtos.subscription.SubscriptionResponse;
import com.projects.lovable_clone.entity.Plan;
import com.projects.lovable_clone.entity.UserSubscription;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SubscriptionMapper {

    SubscriptionResponse toSubscriptionResponse(UserSubscription userSubscription);

    PlanResponse toPlanResponse(Plan plan);

}
