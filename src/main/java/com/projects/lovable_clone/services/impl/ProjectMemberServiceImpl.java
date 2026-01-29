package com.projects.lovable_clone.services.impl;

import com.projects.lovable_clone.dtos.member.InviteMemberRequest;
import com.projects.lovable_clone.dtos.member.MemberResponse;
import com.projects.lovable_clone.dtos.member.UpdateMemberRoleRequest;
import com.projects.lovable_clone.services.ProjectMemberService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectMemberServiceImpl implements ProjectMemberService {


    @Override
    public List<MemberResponse> getProjectMembers(Long projectId, Long userId) {
        return List.of();
    }

    @Override
    public MemberResponse inviteMember(Long projectId, InviteMemberRequest inviteMemberRequest, Long userId) {
        return null;
    }

    @Override
    public MemberResponse updateMemberRole(Long projectId, Long memberId, UpdateMemberRoleRequest updateMemberRoleRequest, Long userId) {
        return null;
    }

    @Override
    public MemberResponse deleteProjectMember(Long projectId, Long memberId, Long userId) {
        return null;
    }
}
