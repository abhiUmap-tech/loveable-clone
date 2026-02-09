package com.projects.lovable_clone.services.impl;

import com.projects.lovable_clone.dtos.project.ProjectRequest;
import com.projects.lovable_clone.dtos.project.ProjectResponse;
import com.projects.lovable_clone.dtos.project.ProjectSummaryResponse;
import com.projects.lovable_clone.entity.Project;
import com.projects.lovable_clone.entity.ProjectMember;
import com.projects.lovable_clone.entity.ProjectMemberId;
import com.projects.lovable_clone.enums.ProjectRole;
import com.projects.lovable_clone.error.ResourceNotFoundException;
import com.projects.lovable_clone.mapper.ProjectMapper;
import com.projects.lovable_clone.repository.ProjectMemberRepository;
import com.projects.lovable_clone.repository.ProjectRepository;
import com.projects.lovable_clone.repository.UserRepository;
import com.projects.lovable_clone.security.AuthUtil;
import com.projects.lovable_clone.services.ProjectService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
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
    ProjectMemberRepository projectMemberRepository;
    AuthUtil authUtil;

    @Override
    public ProjectResponse createProject(ProjectRequest projectRequest) {
        var userId = authUtil.getCurrentUserId();
        var owner = userRepository.getReferenceById(userId);

        var project = Project.builder()
                .name(projectRequest.name())
                .isPublic(false)
                .build();
        var savedProject = projectRepository.save(project);

        var projectMemberId = new ProjectMemberId(project.getId(), owner.getId());

        var projectMember = ProjectMember.builder()
                .id(projectMemberId)
                .project(project)
                .user(owner)
                .projectRole(ProjectRole.OWNER)
                .acceptedAt(Instant.now())
                .invitedAt(Instant.now())
                .build();

        projectMemberRepository.save(projectMember);
        return projectMapper.projectToProjectResponse(savedProject);
    }

    @Override
    public List<ProjectSummaryResponse> getUserProjects() {
        var projects = projectRepository.findAllAccessibleByUser(authUtil.getCurrentUserId());
        return projectMapper.listProjectsToListOfProjectSummaryResponse(projects);
    }

    @Override
    @PreAuthorize("@security.canViewProject(#projectId)")
    public ProjectResponse getUserProjectsById(Long projectId) {
        var project = getAccessibleProjectsById(projectId, authUtil.getCurrentUserId());
        return projectMapper.projectToProjectResponse(project);
    }


    @Override
    @PreAuthorize("@security.canEditProject(#projectId)")
    public ProjectResponse updateProject(Long projectId, ProjectRequest projectRequest) {
        var project = getAccessibleProjectsById(projectId, authUtil.getCurrentUserId());

        project.setName(projectRequest.name());
        var updatedProject = projectRepository.save(project);
        return projectMapper.projectToProjectResponse(updatedProject);
    }

    @Override
    @PreAuthorize("@security.canDeleteProject(#projectId)")
    public String softDelete(Long projectId) {
        var project = getAccessibleProjectsById(projectId, authUtil.getCurrentUserId());

        project.setDeletedAt(Instant.now());
        projectRepository.save(project);
        return "Project Deleted";

    }

    @Override
    public String restoreProject(Long projectId) {
        var project = projectRepository.findByIdIncludingDeleted(projectId, authUtil.getCurrentUserId())
                .orElseThrow();

        project.setDeletedAt(null);
        projectRepository.save(project);

        return "Project Restored";
    }

    //INTERNAL FUNCTIONS
    /// “Give me a project with this ID, owned by this user,
    /// that is not deleted — and also load the owner details immediately.”

    public Project getAccessibleProjectsById(Long projectId, Long userId){
        return projectRepository.findAccessibleProjectsById(projectId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", projectId.toString()));
    }
}
