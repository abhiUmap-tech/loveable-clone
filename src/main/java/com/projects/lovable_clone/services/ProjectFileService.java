package com.projects.lovable_clone.services;

import com.projects.lovable_clone.dtos.project.FileContentResponse;
import com.projects.lovable_clone.dtos.project.FileNode;
import com.projects.lovable_clone.dtos.project.FileTreeResponse;

import java.util.List;

public interface ProjectFileService {
     FileTreeResponse getFileTree(Long projectId);

    FileContentResponse getFileContent(Long projectId, String path);

    void saveFile(Long projectId, String filePath, String fileContent);
}
