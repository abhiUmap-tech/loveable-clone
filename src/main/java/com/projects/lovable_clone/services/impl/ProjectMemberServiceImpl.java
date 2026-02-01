package com.projects.lovable_clone.services.impl;

import com.projects.lovable_clone.dtos.member.InviteMemberRequest;
import com.projects.lovable_clone.dtos.member.MemberResponse;
import com.projects.lovable_clone.dtos.member.UpdateMemberRoleRequest;
import com.projects.lovable_clone.entity.Project;
import com.projects.lovable_clone.entity.ProjectMember;
import com.projects.lovable_clone.entity.ProjectMemberId;
import com.projects.lovable_clone.mapper.ProjectMemberMapper;
import com.projects.lovable_clone.repository.ProjectMemberRepository;
import com.projects.lovable_clone.repository.ProjectRepository;
import com.projects.lovable_clone.repository.UserRepository;
import com.projects.lovable_clone.services.ProjectMemberService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Transactional
public class ProjectMemberServiceImpl implements ProjectMemberService {

    ProjectMemberRepository projectMemberRepository;
    ProjectRepository projectRepository;
    ProjectMemberMapper projectMemberMapper;
    UserRepository userRepository;

    @Override
    public List<MemberResponse> getProjectMembers(Long projectId, Long userId) {
        var project = getAccessibleProjectById(projectId, userId);

        List<MemberResponse> memberResponseList = new ArrayList<>();
        memberResponseList.add(projectMemberMapper.toProjectMemberResponseFromOwner(project.getOwner()));

        memberResponseList.addAll(
                projectMemberRepository.findByIdProjectId(projectId)
                .stream()
                .map(projectMemberMapper::toProjectMemberResponseFromMember)
                .toList());


        return memberResponseList;
    }

    @Override
    public MemberResponse inviteMember(Long projectId, InviteMemberRequest inviteMemberRequest, Long userId) {
        var project = getAccessibleProjectById(projectId, userId);

        if(!project.getOwner().getId().equals(userId))
            throw new RuntimeException("Not allowed");

        var invitee = userRepository.findByEmail(inviteMemberRequest.email())
                .orElseThrow();

        if (invitee.getId().equals(userId))
            throw new RuntimeException("Cannot invite yourself");

        var projectMemberId = new ProjectMemberId(projectId, invitee.getId());

        if (projectMemberRepository.existsById(projectMemberId))
            throw new RuntimeException("Already invited");

        var projectMember = ProjectMember.builder()
                .id(projectMemberId)
                .project(project)
                .user(invitee)
                .projectRole(inviteMemberRequest.projectRole())
                .invitedAt(Instant.now())
                .build();

        var savedProjectMember = projectMemberRepository.save(projectMember);
        return projectMemberMapper.toProjectMemberResponseFromMember(savedProjectMember);
    }

    @Override
    public MemberResponse updateMemberRole(Long projectId, Long memberId, UpdateMemberRoleRequest updateMemberRoleRequest, Long userId) {
        var project = getAccessibleProjectById(projectId, userId);

        //Check if they are project owner
        if(!project.getOwner().getId().equals(userId))
            throw new RuntimeException("You are not the Project Owner");

        var projectMemberId = new ProjectMemberId(projectId, memberId);

        var projectMember = projectMemberRepository.findById(projectMemberId)
                .orElseThrow();

        projectMember.setProjectRole(updateMemberRoleRequest.projectRole());
        projectMemberRepository.save(projectMember);

        return projectMemberMapper.toProjectMemberResponseFromMember(projectMember);
    }

    @Override
    public String removeProjectMember(Long projectId, Long memberId, Long userId) throws AccessDeniedException {
        //Get the Project
        var project = getAccessibleProjectById(projectId, userId);

        //Check whether the person is the owner or not
        if (!project.getOwner().getId().equals(userId))
            throw new AccessDeniedException("You are not the Owner of the Project");

        var projectMemberId = new ProjectMemberId(projectId, memberId);

        var projectMember = projectMemberRepository.findById(projectMemberId)
                .orElseThrow(() ->
                        new RuntimeException("Project member does not exist"));

        projectMemberRepository.delete(projectMember);
        return "Project member with id " + memberId + " deleted successfully";
    }


    //INTERNAL FUNCTIONS
    /// “Give me a project with this ID, owned by this user,
    /// that is not deleted — and also load the owner details immediately.”
    public Project getAccessibleProjectById(Long projectId, Long userId){
        return projectRepository.findAccessibleProjectsById(projectId, userId)
            .orElseThrow();
    }
}
