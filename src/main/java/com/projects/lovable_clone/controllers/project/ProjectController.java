package com.projects.lovable_clone.controllers.project;

import com.projects.lovable_clone.dtos.project.ProjectRequest;
import com.projects.lovable_clone.dtos.project.ProjectResponse;
import com.projects.lovable_clone.dtos.project.ProjectSummaryResponse;
import com.projects.lovable_clone.services.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;

    @GetMapping
    public ResponseEntity<List<ProjectSummaryResponse>> getMyProject(){
        Long userId = 1L;//TODO : update later with Spring Security
        return ResponseEntity.ok(projectService.getUserProjects(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponse> getProjectById(@PathVariable  Long id){
        //TODO : Get Projects by Id provided
        Long userId = 1L;
        return ResponseEntity.ok(projectService.getUserProjectsById(id, userId));
    }

    @PostMapping
    public ResponseEntity<ProjectResponse> createProject(@RequestBody ProjectRequest projectRequest){
        Long userId = 1L;
        return ResponseEntity.status(HttpStatus.CREATED).body(projectService.createProject(projectRequest, userId));
    }

    @PatchMapping("/{projectId}")
    public ResponseEntity<ProjectResponse> updateProject(@PathVariable Long projectId, @RequestBody ProjectRequest projectRequest){
        Long userId = 1L;
        return ResponseEntity.ok(projectService.updateProject(projectId, projectRequest, userId));
    }

    @PutMapping("/{projectId}/restore")
    public ResponseEntity<String> restoreProject(@PathVariable Long projectId){
        Long userId = 1L;
        return ResponseEntity.ok(projectService.restoreProject(projectId, userId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProject(@PathVariable Long id){
        Long userId = 1L;
        return ResponseEntity.ok(projectService.softDelete(id, userId));

    }




}
