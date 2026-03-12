package com.projects.lovable_clone.mapper;

import com.projects.lovable_clone.dtos.project.FileNode;
import com.projects.lovable_clone.entity.ProjectFile;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProjectFileMapper {

    List<FileNode> toListOfFileNode(List<ProjectFile> projectFileList);
}
