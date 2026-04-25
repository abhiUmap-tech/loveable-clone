package com.projects.lovable_clone.services;

import com.projects.lovable_clone.dtos.deploy.DeployResponse;

public interface DeploymentService {

    DeployResponse deploy(Long projectId);

}
