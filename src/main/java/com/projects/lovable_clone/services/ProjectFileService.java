package com.projects.lovable_clone.services;

import com.projects.lovable_clone.dtos.project.FileContentResponse;
import com.projects.lovable_clone.dtos.project.FileNode;

import java.util.List;

public interface ProjectFileService {
     List<FileNode> getFileTree(Long projectId, Long userId);

    FileContentResponse getFileContent(Long projectId, String path, Long userId);

    void saveFile(Long projectId, String filePath, String fileContent);
}
