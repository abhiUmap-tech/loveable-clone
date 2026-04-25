package com.projects.lovable_clone.controllers.files;

import com.projects.lovable_clone.dtos.project.FileContentResponse;
import com.projects.lovable_clone.dtos.project.FileNode;
import com.projects.lovable_clone.dtos.project.FileTreeResponse;
import com.projects.lovable_clone.services.ProjectFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/projects/{projectId}/files")
public class FileController {

    private final ProjectFileService fileService;
    private final ProjectFileService projectFileService;

    @GetMapping
    public ResponseEntity<FileTreeResponse> getFileTree(@PathVariable Long projectId){
        return ResponseEntity.ok(fileService.getFileTree(projectId));
    }

    @GetMapping("/content")
    public ResponseEntity<FileContentResponse> getFile(
            @PathVariable Long projectId,
            @RequestParam String path){
        return ResponseEntity.ok(projectFileService.getFileContent(projectId, path));
    }


}
