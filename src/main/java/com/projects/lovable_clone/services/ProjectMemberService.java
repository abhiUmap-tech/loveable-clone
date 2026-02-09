package com.projects.lovable_clone.services;

import com.projects.lovable_clone.dtos.member.InviteMemberRequest;
import com.projects.lovable_clone.dtos.member.MemberResponse;
import com.projects.lovable_clone.dtos.member.UpdateMemberRoleRequest;

import java.nio.file.AccessDeniedException;
import java.util.List;

public interface ProjectMemberService {
    List<MemberResponse> getProjectMembers(Long projectId);

    MemberResponse inviteMember(Long projectId, InviteMemberRequest inviteMemberRequest);


    MemberResponse updateMemberRole(Long projectId, Long memberId, UpdateMemberRoleRequest updateMemberRoleRequest);

    String removeProjectMember(Long projectId, Long memberId) throws AccessDeniedException;
}
