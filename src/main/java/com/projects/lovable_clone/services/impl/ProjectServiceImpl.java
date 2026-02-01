package com.projects.lovable_clone.services.impl;

import com.projects.lovable_clone.dtos.project.ProjectRequest;
import com.projects.lovable_clone.dtos.project.ProjectResponse;
import com.projects.lovable_clone.dtos.project.ProjectSummaryResponse;
import com.projects.lovable_clone.entity.Project;
import com.projects.lovable_clone.mapper.ProjectMapper;
import com.projects.lovable_clone.repository.ProjectRepository;
import com.projects.lovable_clone.repository.UserRepository;
import com.projects.lovable_clone.services.ProjectService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
public class ProjectServiceImpl implements ProjectService {

    ProjectRepository projectRepository;
    UserRepository userRepository;
    ProjectMapper projectMapper;

    @Override
    public ProjectResponse createProject(ProjectRequest projectRequest, Long userId) {
        var owner = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        var project = Project.builder()
                .name(projectRequest.name())
                .isPublic(false)
                .owner(owner)
                .build();

        var savedProject = projectRepository.save(project);
        return projectMapper.projectToProjectResponse(savedProject);
    }

    @Override
    public List<ProjectSummaryResponse> getUserProjects(Long userId) {
        var projects = projectRepository.findAllAccessibleByUser(userId);
        return projectMapper.listProjectsToListOfProjectSummaryResponse(projects);

    }

    @Override
    public ProjectResponse getUserProjectsById(Long projectId, Long userId) {
        var project = getAccessibleProjectsById(projectId, userId);
        return projectMapper.projectToProjectResponse(project);
    }



    @Override
    public ProjectResponse updateProject(Long projectId, ProjectRequest projectRequest, Long userId) {
        var project = getAccessibleProjectsById(projectId, userId);

        project.setName(projectRequest.name());
        var updatedProject = projectRepository.save(project);
        return projectMapper.projectToProjectResponse(updatedProject);
    }

    @Override
    public String softDelete(Long id, Long userId) {
        var project = getAccessibleProjectsById(id, userId);

        if (!project.getOwner().getId().equals(userId))
            throw new RuntimeException("You are not allowed to delete");

        project.setDeleteAt(Instant.now());
        projectRepository.save(project);
        return "Project Deleted";

    }

    @Override
    public String restoreProject(Long projectId, Long userId) {
        var project = projectRepository.findByIdIncludingDeleted(projectId, userId)
                .orElseThrow();

        if(!project.getOwner().getId().equals(userId))
            throw new RuntimeException("You are not allowed to restore");

        project.setDeleteAt(null);
        projectRepository.save(project);

        return "Project Restored";
    }

    //INTERNAL FUNCTIONS
    /// “Give me a project with this ID, owned by this user,
    /// that is not deleted — and also load the owner details immediately.”

    public Project getAccessibleProjectsById(Long projectId, Long userId){
        return projectRepository.findAccessibleProjectsById(projectId, userId)
                .orElseThrow();
    }
}
