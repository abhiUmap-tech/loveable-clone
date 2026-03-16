package com.projects.lovable_clone.security;

import com.projects.lovable_clone.enums.ProjectPermission;
import com.projects.lovable_clone.repository.ProjectMemberRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component("security")
@SuppressWarnings("unused")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SecurityExpressions {

    ProjectMemberRepository projectMemberRepository;
    AuthUtil authUtil;

    private boolean hasPermission(Long projectId, ProjectPermission projectPermission) {
        var userId = authUtil.getCurrentUserId();
        log.info("Checking permission: projectId={}, userId={}, permission={}", projectId, userId, projectPermission);

        return projectMemberRepository.findRoleByProjectIdAndUserId(projectId, userId)
                .map(role -> {
                    log.info("Role found: {}, permissions: {}", role, role.getPermissions());
                    return role.getPermissions().contains(projectPermission);
                })
                .orElse(false); // <-- if this fires, user is not a project member
    }

    public boolean canViewProject(Long projectId){
       return hasPermission(projectId, ProjectPermission.VIEW);
    }

    public boolean canEditProject(Long projectId){
      return hasPermission(projectId, ProjectPermission.EDIT);
    }

    public boolean canDeleteProject(Long projectId){
        return hasPermission(projectId, ProjectPermission.DELETE);
    }

    public boolean canViewMembers(Long projectId){
        return hasPermission(projectId, ProjectPermission.VIEW_MEMBERS);
    }

    public boolean canManageMembers(Long projectId){
        return hasPermission(projectId, ProjectPermission.MANAGE_MEMBERS);
    }




}
