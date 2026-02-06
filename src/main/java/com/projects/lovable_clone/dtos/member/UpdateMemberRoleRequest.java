package com.projects.lovable_clone.dtos.member;

import com.projects.lovable_clone.enums.ProjectRole;
import jakarta.validation.constraints.NotNull;

public record UpdateMemberRoleRequest(
        @NotNull ProjectRole projectRole

) {
}
