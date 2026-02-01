package com.projects.lovable_clone.services;

import com.projects.lovable_clone.dtos.member.InviteMemberRequest;
import com.projects.lovable_clone.dtos.member.MemberResponse;
import com.projects.lovable_clone.dtos.member.UpdateMemberRoleRequest;

import java.nio.file.AccessDeniedException;
import java.util.List;

public interface ProjectMemberService {
    List<MemberResponse> getProjectMembers(Long projectId, Long userId);

    MemberResponse inviteMember(Long projectId, InviteMemberRequest inviteMemberRequest, Long userId);


    MemberResponse updateMemberRole(Long projectId, Long memberId, UpdateMemberRoleRequest updateMemberRoleRequest,Long userId);

    String removeProjectMember(Long projectId, Long memberId, Long userId) throws AccessDeniedException;
}
