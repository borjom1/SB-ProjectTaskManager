package com.example.projecttaskmanager.service.impl;

import com.example.projecttaskmanager.dto.NewProjectDto;
import com.example.projecttaskmanager.dto.ProjectDto;
import com.example.projecttaskmanager.entity.ProjectEntity;
import com.example.projecttaskmanager.entity.UserEntity;
import com.example.projecttaskmanager.exception.UserNotFoundException;
import com.example.projecttaskmanager.repository.ProjectRepository;
import com.example.projecttaskmanager.service.ProjectService;
import com.example.projecttaskmanager.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ModelMapper mapper;
    private final ProjectRepository projectRepository;
    private final UserService userService;

    @Override
    public List<ProjectDto> getProjects(Long userId) throws UserNotFoundException {
        log.info("getProjects(): user-id={}", userId);

        UserEntity user = userService.findUserById(userId);
        return user.getProjects().stream()
                .map(this::extractDto)
                .toList();
    }

    @Override
    public ProjectDto createProject(NewProjectDto dto, Long creatorId) throws UserNotFoundException {
        log.info("createProject(): body={}", dto);

        UserEntity creator = userService.findUserById(creatorId);
        ProjectEntity project = mapper.map(dto, ProjectEntity.class);

        project.addMember(creator);
        projectRepository.save(project);
        return extractDto(project);
    }

    private ProjectDto extractDto(ProjectEntity project) {
        return new ProjectDto(
                project.getId(),
                project.getName(),
                project.getDescription(),
                project.getUsers().size(),
                project.getTasksDone(),
                project.getTasks() - project.getTasksDone()
        );
    }

}
