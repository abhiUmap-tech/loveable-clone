package com.projects.lovable_clone.services;

import com.projects.lovable_clone.dtos.member.InviteMemberRequest;
import com.projects.lovable_clone.dtos.member.MemberResponse;
import com.projects.lovable_clone.dtos.member.UpdateMemberRoleRequest;
import org.jspecify.annotations.Nullable;

import java.util.List;

public interface ProjectMemberService {
    List<MemberResponse> getProjectMembers(Long projectId, Long userId);

    MemberResponse inviteMember(Long projectId, InviteMemberRequest inviteMemberRequest, Long userId);


    MemberResponse updateMemberRole(Long projectId, Long memberId, InviteMemberRequest inviteMemberRequest,Long userId);

    MemberResponse deleteProjectMember(Long projectId, Long memberId, Long userId);
}
