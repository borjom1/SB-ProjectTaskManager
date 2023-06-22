package com.example.projecttaskmanager.controller;

import com.example.projecttaskmanager.dto.NewProjectDto;
import com.example.projecttaskmanager.dto.ProjectDto;
import com.example.projecttaskmanager.exception.UserNotFoundException;
import com.example.projecttaskmanager.security.UserDetailsImpl;
import com.example.projecttaskmanager.service.ProjectService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @GetMapping
    public List<ProjectDto> getProjects() throws UserNotFoundException {
        return projectService.getProjects(getPrincipal().getId());
    }

    @PostMapping
    @RolesAllowed({"MANAGER", "ADMIN"})
    public ProjectDto createProject(@Valid @RequestBody NewProjectDto dto) throws UserNotFoundException {
        return projectService.createProject(dto, getPrincipal().getId());
    }

    private UserDetailsImpl getPrincipal() {
        return (UserDetailsImpl) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
    }

}