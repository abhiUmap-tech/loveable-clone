package com.projects.lovable_clone.dtos.member;

import com.projects.lovable_clone.enums.ProjectRole;

public record InviteMemberRequest(
        String email,
        ProjectRole projectRole
) {
}
