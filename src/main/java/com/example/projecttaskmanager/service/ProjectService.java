package com.example.projecttaskmanager.service;

import com.example.projecttaskmanager.dto.NewProjectDto;
import com.example.projecttaskmanager.dto.ProjectDto;
import com.example.projecttaskmanager.exception.UserNotFoundException;

import java.util.List;

public interface ProjectService {
    List<ProjectDto> getProjects(Long userId) throws UserNotFoundException;
    ProjectDto createProject(NewProjectDto dto, Long creatorId) throws UserNotFoundException;
}