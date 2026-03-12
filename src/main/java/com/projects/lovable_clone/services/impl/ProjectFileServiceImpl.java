package com.projects.lovable_clone.services.impl;

import com.projects.lovable_clone.dtos.project.FileContentResponse;
import com.projects.lovable_clone.dtos.project.FileNode;
import com.projects.lovable_clone.entity.ProjectFile;
import com.projects.lovable_clone.error.ResourceNotFoundException;
import com.projects.lovable_clone.mapper.ProjectFileMapper;
import com.projects.lovable_clone.repository.ProjectFileRepository;
import com.projects.lovable_clone.repository.ProjectRepository;
import com.projects.lovable_clone.services.ProjectFileService;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProjectFileServiceImpl implements ProjectFileService {

    private final ProjectRepository projectRepository;
    private final ProjectFileRepository projectFileRepository;
    private final MinioClient minioClient;
    private final ProjectFileMapper projectFileMapper;

    @Value("${minio.project-bucket}")
    private String minioProjectBucket;

    @Override
    public List<FileNode> getFileTree(Long projectId, Long userId) {
        var projectFiles = projectFileRepository.findByProjectId(projectId);
        return projectFileMapper.toListOfFileNode(projectFiles);
    }

    @Override
    public FileContentResponse getFileContent(Long projectId, String path, Long userId) {
        return null;
    }

    @Override
    public void saveFile(Long projectId, String path, String content) {
        var project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", projectId.toString()));

        String cleanPath = path.startsWith("/") ? path.substring(1) : path;
        String objectKey = projectId + "/" + cleanPath;

        try {

            var contentBytes = content.getBytes(StandardCharsets.UTF_8);
            var inputStream = new ByteArrayInputStream(contentBytes);

            //saving the file content
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(minioProjectBucket)
                            .object(objectKey)
                            .stream(inputStream, contentBytes.length, -1)
                            .contentType(determineContentType(path))
                            .build());

            //Saving the metaData
            ProjectFile file = projectFileRepository.findByProjectIdAndPath(projectId, cleanPath)
                    .orElseGet(() -> ProjectFile.builder()
                            .project(project)
                            .path(cleanPath)
                            .minioObjectKey(objectKey)
                            .createdAt(Instant.now())
                            .build());

            file.setUpdatedAt(Instant.now());
            projectFileRepository.save(file);


        } catch (Exception e) {
            log.error("Failed to save file {}/{}", projectId, cleanPath, e);
            throw new RuntimeException("File save failed", e);
        }


    }


    private String determineContentType(String path) {
        var type = URLConnection.guessContentTypeFromName(path);

        if (type != null) return type;
        if (path.endsWith(".jsx") || path.endsWith(".ts") || path.endsWith(".tsx")) return "text/javascript";
        if (path.endsWith(".json")) return "application/json";
        if (path.endsWith(".css")) return "text/css";

        return "text/plain";

    }
}
