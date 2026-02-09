package com.projects.lovable_clone.controllers.project;

import com.projects.lovable_clone.dtos.project.ProjectRequest;
import com.projects.lovable_clone.dtos.project.ProjectResponse;
import com.projects.lovable_clone.dtos.project.ProjectSummaryResponse;
import com.projects.lovable_clone.services.ProjectService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/api/projects")
public class ProjectController {

    ProjectService projectService;


    @GetMapping
    public ResponseEntity<List<ProjectSummaryResponse>> getMyProject(){
        return ResponseEntity.ok(projectService.getUserProjects());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponse> getProjectById(@PathVariable  Long id){
        return ResponseEntity.ok(projectService.getUserProjectsById(id));
    }

    @PostMapping
    public ResponseEntity<ProjectResponse> createProject(@RequestBody @Valid ProjectRequest projectRequest){

        return ResponseEntity.status(HttpStatus.CREATED).body(projectService.createProject(projectRequest));
    }

    @PatchMapping("/{projectId}")
    public ResponseEntity<ProjectResponse> updateProject(@PathVariable Long projectId, @RequestBody @Valid ProjectRequest projectRequest){

        return ResponseEntity.ok(projectService.updateProject(projectId, projectRequest));
    }

    @PutMapping("/{projectId}/restore")
    public ResponseEntity<String> restoreProject(@PathVariable Long projectId){

        return ResponseEntity.ok(projectService.restoreProject(projectId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProject(@PathVariable Long id){

        return ResponseEntity.ok(projectService.softDelete(id));

    }




}
