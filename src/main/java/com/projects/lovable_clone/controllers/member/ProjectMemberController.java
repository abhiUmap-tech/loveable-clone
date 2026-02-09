package com.projects.lovable_clone.controllers.member;

import com.projects.lovable_clone.dtos.member.InviteMemberRequest;
import com.projects.lovable_clone.dtos.member.MemberResponse;
import com.projects.lovable_clone.dtos.member.UpdateMemberRoleRequest;
import com.projects.lovable_clone.services.ProjectMemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/project/{projectId}/members")
public class ProjectMemberController {

    private final ProjectMemberService projectMemberService;

    @GetMapping
    public ResponseEntity<List<MemberResponse>> getProjectMember(@PathVariable Long projectId){
        return ResponseEntity.ok(projectMemberService.getProjectMembers(projectId));
    }

    @PostMapping
    public ResponseEntity<MemberResponse> inviteMember(
            @PathVariable Long projectId,
            @RequestBody @Valid InviteMemberRequest inviteMemberRequest){

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(projectMemberService.inviteMember(projectId, inviteMemberRequest));


    }

    @PatchMapping("/{memberId}")
    public ResponseEntity<MemberResponse> updateMemberRole(
            @PathVariable Long projectId,
            @PathVariable Long memberId,
            @RequestBody @Valid UpdateMemberRoleRequest updateMemberRoleRequest){
        return ResponseEntity.ok(projectMemberService.updateMemberRole(projectId, memberId,updateMemberRoleRequest));

    }

  @DeleteMapping("/{memberId}")
    public ResponseEntity<String> deleteMemberRole(
            @PathVariable Long projectId,
            @PathVariable Long memberId) throws AccessDeniedException {
        return ResponseEntity.ok(projectMemberService.removeProjectMember(projectId, memberId));

    }



}
