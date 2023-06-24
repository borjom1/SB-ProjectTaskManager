package com.example.projecttaskmanager.service;

import com.example.projecttaskmanager.dto.NewProjectDto;
import com.example.projecttaskmanager.dto.ProjectDto;
import com.example.projecttaskmanager.dto.StoryDto;
import com.example.projecttaskmanager.dto.TaskDto;
import com.example.projecttaskmanager.exception.FakeMemberException;
import com.example.projecttaskmanager.exception.StoryNotFoundException;
import com.example.projecttaskmanager.exception.UserNotFoundException;

import java.util.List;

public interface ProjectService {

    ProjectDto getProject(Long projectId, Long userId) throws FakeMemberException, UserNotFoundException;

    List<ProjectDto> getProjects(Long userId) throws UserNotFoundException;

    ProjectDto createProject(NewProjectDto dto, Long creatorId) throws UserNotFoundException;

    List<TaskDto> getTasks(Long projectId, Long storyId, Long userId)
            throws FakeMemberException, UserNotFoundException, StoryNotFoundException;

    List<StoryDto> getStories(Long projectId, Long userId) throws FakeMemberException, UserNotFoundException;

    StoryDto getFirstStory(Long projectId, Long userId) throws FakeMemberException, UserNotFoundException;

}