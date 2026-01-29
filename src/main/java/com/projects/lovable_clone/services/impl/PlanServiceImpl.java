package com.projects.lovable_clone.services.impl;

import com.projects.lovable_clone.dtos.subscription.PlanResponse;
import com.projects.lovable_clone.services.PlanService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlanServiceImpl implements PlanService {
    @Override
    public List<PlanResponse> getAllActivePlans() {
        return List.of();
    }
}
