package com.example.projecttaskmanager.service;

import com.example.projecttaskmanager.dto.*;
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
    StoryDto createStory(NewStoryDto dto, Long projectId, Long userId) throws FakeMemberException, UserNotFoundException;
    TaskDto createTask(NewTaskDto dto, Long userId) throws StoryNotFoundException, FakeMemberException, UserNotFoundException;

    List<MemberDto> getMembers(Long projectId, Long userId) throws FakeMemberException, UserNotFoundException;

    void addMember(Long projectId, Long newUserId, Long userId) throws FakeMemberException, UserNotFoundException;
    void removeMember(Long projectId, Long memberId, Long userId) throws FakeMemberException, UserNotFoundException;

}