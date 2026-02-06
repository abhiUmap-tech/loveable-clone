package com.projects.lovable_clone.mapper;

import com.projects.lovable_clone.dtos.project.ProjectResponse;
import com.projects.lovable_clone.dtos.project.ProjectSummaryResponse;
import com.projects.lovable_clone.entity.Project;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProjectMapper {

    //@Mapping(target = "", source = "")
    ProjectResponse projectToProjectResponse(Project project);


    ProjectSummaryResponse projectToProjectSummaryResponse(Project project);

    List<ProjectSummaryResponse> listProjectsToListOfProjectSummaryResponse(List<Project> projects);
}
