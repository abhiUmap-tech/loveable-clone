package com.projects.lovable_clone.services;

import com.projects.lovable_clone.dtos.subscription.PlanResponse;
import org.jspecify.annotations.Nullable;

import java.util.List;

public interface PlanService {
    List<PlanResponse> getAllActivePlans();
}
