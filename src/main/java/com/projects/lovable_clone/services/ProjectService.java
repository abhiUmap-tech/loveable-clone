package com.projects.lovable_clone.services;

import com.projects.lovable_clone.dtos.project.ProjectRequest;
import com.projects.lovable_clone.dtos.project.ProjectResponse;
import com.projects.lovable_clone.dtos.project.ProjectSummaryResponse;

import java.util.List;

public interface ProjectService {

    List<ProjectSummaryResponse> getUserProjects();

    ProjectResponse getUserProjectsById(Long projectId);

    ProjectResponse createProject(ProjectRequest projectRequest);

    ProjectResponse updateProject(Long projectId, ProjectRequest projectRequest);

    String softDelete(Long projectId);

    String restoreProject(Long projectId);
}
