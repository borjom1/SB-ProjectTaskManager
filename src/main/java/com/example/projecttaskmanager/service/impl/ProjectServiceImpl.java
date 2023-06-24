package com.example.projecttaskmanager.service.impl;

import com.example.projecttaskmanager.dto.NewProjectDto;
import com.example.projecttaskmanager.dto.ProjectDto;
import com.example.projecttaskmanager.dto.StoryDto;
import com.example.projecttaskmanager.dto.TaskDto;
import com.example.projecttaskmanager.entity.ProjectEntity;
import com.example.projecttaskmanager.entity.StoryEntity;
import com.example.projecttaskmanager.entity.UserEntity;
import com.example.projecttaskmanager.exception.FakeMemberException;
import com.example.projecttaskmanager.exception.StoryNotFoundException;
import com.example.projecttaskmanager.exception.UserNotFoundException;
import com.example.projecttaskmanager.repository.ProjectRepository;
import com.example.projecttaskmanager.service.ProjectService;
import com.example.projecttaskmanager.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static java.util.Comparator.comparing;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ModelMapper mapper;
    private final ProjectRepository projectRepository;
    private final UserService userService;

    @Override
    public ProjectDto getProject(Long projectId, Long userId) throws FakeMemberException, UserNotFoundException {
        log.info("getProject(): projectId={}, user-id={}", projectId, userId);

        UserEntity user = userService.findUserById(userId);
        ProjectEntity foundProject = findProject(projectId, user);
        return extractDto(foundProject);
    }

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

    @Override
    public List<TaskDto> getTasks(Long projectId, Long storyId, Long userId)
            throws FakeMemberException, StoryNotFoundException, UserNotFoundException {
        log.info("getTasks(): projectId={}, storyId={}, userId={}", projectId, storyId, userId);

        UserEntity user = userService.findUserById(userId);
        ProjectEntity project = findProject(projectId, user);

        StoryEntity foundStory = project.getStories().stream()
                .filter(story -> story.getId().equals(storyId))
                .findAny()
                .orElseThrow(() -> new StoryNotFoundException("story not found"));

        return foundStory.getTasks().stream()
                .map(task -> {
                    UserEntity assignedUser = task.getAssignedUser();
                    return new TaskDto(
                            task.getId(),
                            task.getTitle(),
                            task.getStatus(),
                            task.getAssignedUser().getId(),
                            "%s %s".formatted(assignedUser.getFirstName(), assignedUser.getLastName()),
                            task.getMarks()
                    );
                }).toList();
    }

    @Override
    public List<StoryDto> getStories(Long projectId, Long userId) throws FakeMemberException, UserNotFoundException {
        log.info("getStories(): projectId={}, userId={}", projectId, userId);

        UserEntity user = userService.findUserById(userId);
        ProjectEntity project = findProject(projectId, user);

        return project.getStories().stream()
                .sorted(comparing(StoryEntity::getCreatedAt))
                .map(story -> {
                    StoryDto mappedDto = mapper.map(story, StoryDto.class);
                    mappedDto.setTasksCount(story.getTasks().size());
                    return mappedDto;
                })
                .toList();
    }

    @Override
    public StoryDto getFirstStory(Long projectId, Long userId) throws FakeMemberException, UserNotFoundException {
        log.info("getFirstStory(): projectId={}, userId={}", projectId, userId);

        UserEntity user = userService.findUserById(userId);
        ProjectEntity project = findProject(projectId, user);

        Optional<StoryEntity> optStory = project.getStories()
                .stream()
                .min(comparing(StoryEntity::getCreatedAt));

        StoryDto dto = null;
        if (optStory.isPresent()) {
            dto = mapper.map(optStory.get(), StoryDto.class);
            dto.setTasksCount(optStory.get().getTasks().size());
        }
        return dto;
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

    private ProjectEntity findProject(Long projectId, UserEntity user) throws FakeMemberException {
        return user.getProjects().stream()
                .filter(project -> project.getId().equals(projectId))
                .findAny()
                .orElseThrow(() -> new FakeMemberException("you are not a member of the project"));
    }

}