package com.example.projecttaskmanager.controller;

import com.example.projecttaskmanager.dto.*;
import com.example.projecttaskmanager.entity.TaskMark;
import com.example.projecttaskmanager.entity.TaskStatus;
import com.example.projecttaskmanager.exception.*;
import com.example.projecttaskmanager.security.UserDetailsImpl;
import com.example.projecttaskmanager.service.ProjectService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.NO_CONTENT;

@RestController
@RequestMapping("/user/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @GetMapping("/{id}")
    public ProjectDto getProject(@PathVariable Long id) throws UserNotFoundException, FakeMemberException {
        return projectService.getProject(id, getPrincipal().getId());
    }

    @GetMapping
    public List<ProjectDto> getProjects() throws UserNotFoundException {
        return projectService.getProjects(getPrincipal().getId());
    }

    @PostMapping
    @RolesAllowed({"MANAGER", "ADMIN"})
    public ProjectDto createProject(@Valid @RequestBody NewProjectDto dto) throws UserNotFoundException {
        return projectService.createProject(dto, getPrincipal().getId());
    }

    @GetMapping("/tasks")
    public List<TaskDto> getTasksWith(@RequestParam Long projectId, @RequestParam Long storyId)
            throws UserNotFoundException, FakeMemberException, StoryNotFoundException {
        return projectService.getTasks(projectId, storyId, getPrincipal().getId());
    }

    @RolesAllowed({"MANAGER", "ADMIN"})
    @PostMapping("/tasks")
    public TaskDto createTask(@Valid @RequestBody NewTaskDto dto)
            throws UserNotFoundException, FakeMemberException, StoryNotFoundException, InvalidRequestBodyException {

        try {
            dto.getMarks().forEach(TaskMark::valueOf);
        } catch (IllegalArgumentException e) {
            throw new InvalidRequestBodyException("invalid mark");
        }

        return projectService.createTask(dto, getPrincipal().getId());
    }

    @Deprecated
    @GetMapping("/{id}/story")
    public StoryDto getFirstStory(@PathVariable Long id) throws UserNotFoundException, FakeMemberException {
        return projectService.getFirstStory(id, getPrincipal().getId());
    }

    @GetMapping("/{id}/stories")
    public List<StoryDto> getStories(@PathVariable Long id) throws UserNotFoundException, FakeMemberException {
        return projectService.getStories(id, getPrincipal().getId());
    }

    @RolesAllowed({"MANAGER", "ADMIN"})
    @PostMapping("/{id}/story")
    public StoryDto createStory(@PathVariable Long id, @Valid @RequestBody NewStoryDto dto)
            throws UserNotFoundException, FakeMemberException {

        return projectService.createStory(dto, id, getPrincipal().getId());
    }

    @GetMapping("/{id}/members")
    public List<MemberDto> getMembers(@PathVariable Long id) throws UserNotFoundException, FakeMemberException {
        return projectService.getMembers(id, getPrincipal().getId());
    }

    @RolesAllowed({"MANAGER", "ADMIN"})
    @PatchMapping("/{projectId}/members/{id}")
    @ResponseStatus(NO_CONTENT)
    public void addMember(@PathVariable Long projectId, @PathVariable Long id) throws UserNotFoundException, FakeMemberException {
        projectService.addMember(projectId, id, getPrincipal().getId());
    }

    @RolesAllowed({"MANAGER", "ADMIN"})
    @DeleteMapping("/{projectId}/members/{id}")
    @ResponseStatus(NO_CONTENT)
    public void removeMember(@PathVariable Long projectId, @PathVariable Long id) throws UserNotFoundException, FakeMemberException {
        projectService.removeMember(projectId, id, getPrincipal().getId());
    }

    @PatchMapping("/story/{storyId}/tasks/{taskId}/assign")
    @ResponseStatus(NO_CONTENT)
    public void assignTask(@PathVariable Long storyId, @PathVariable Long taskId)
            throws UserNotFoundException, FakeMemberException, TaskNotFoundException, StoryNotFoundException {

        projectService.assignTask(storyId, taskId, getPrincipal().getId());
    }

    @PatchMapping("/story/{storyId}/tasks/{taskId}")
    @ResponseStatus(NO_CONTENT)
    public void changeTaskStatus(@PathVariable Long storyId, @PathVariable Long taskId, @RequestParam String status)
            throws
            UserNotFoundException, FakeMemberException, TaskNotFoundException,
            StoryNotFoundException, InvalidRequestBodyException {

        TaskStatus selectedStatus;
        try {
            selectedStatus = TaskStatus.valueOf(status);
        } catch (IllegalArgumentException e) {
            throw new InvalidRequestBodyException("invalid status");
        }
        projectService.changeTaskStatus(storyId, taskId, selectedStatus, getPrincipal().getId());
    }

    private UserDetailsImpl getPrincipal() {
        return (UserDetailsImpl) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
    }

}