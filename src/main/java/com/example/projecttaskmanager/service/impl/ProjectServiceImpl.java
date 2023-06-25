package com.example.projecttaskmanager.service.impl;

import com.example.projecttaskmanager.dto.*;
import com.example.projecttaskmanager.entity.*;
import com.example.projecttaskmanager.exception.FakeMemberException;
import com.example.projecttaskmanager.exception.StoryNotFoundException;
import com.example.projecttaskmanager.exception.TaskNotFoundException;
import com.example.projecttaskmanager.exception.UserNotFoundException;
import com.example.projecttaskmanager.repository.ProjectRepository;
import com.example.projecttaskmanager.repository.StoryRepository;
import com.example.projecttaskmanager.repository.TaskRepository;
import com.example.projecttaskmanager.service.ProjectService;
import com.example.projecttaskmanager.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.example.projecttaskmanager.entity.TaskStatus.DONE;
import static com.example.projecttaskmanager.entity.TaskStatus.NOT_STARTED;
import static java.util.Comparator.comparing;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ModelMapper mapper;

    private final ProjectRepository projectRepository;
    private final StoryRepository storyRepository;
    private final TaskRepository taskRepository;

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
                            assignedUser == null ? null : assignedUser.getId(),
                            assignedUser == null ?
                                    null :
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
                .map(this::extractDto)
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
            dto = extractDto(optStory.get());
        }
        return dto;
    }

    @Override
    public StoryDto createStory(NewStoryDto dto, Long projectId, Long userId) throws FakeMemberException, UserNotFoundException {
        log.info("createStory(): NewStoryDto={}, projectId={}, userId={}", dto, projectId, userId);

        UserEntity user = userService.findUserById(userId);
        ProjectEntity project = findProject(projectId, user);

        StoryEntity newStory = StoryEntity.builder()
                .name(dto.getName())
                .start(dto.getFrom())
                .end(dto.getTo())
                .build();

        project.addStory(newStory);
        storyRepository.save(newStory);
        return extractDto(newStory);
    }

    @Override
    public TaskDto createTask(NewTaskDto dto, Long userId)
            throws StoryNotFoundException, FakeMemberException, UserNotFoundException {
        log.info("createTask(): NewTaskDto={}, userId={}", dto, userId);

        StoryEntity story = storyRepository.findById(dto.getStoryId())
                .orElseThrow(() -> new StoryNotFoundException("story not found"));

        UserEntity user = userService.findUserById(userId);

        checkProjectMembership(user, story.getProject());

        TaskEntity builtTask = TaskEntity.builder()
                .title(dto.getTitle())
                .story(story)
                .marks(dto.getMarks().stream().map(String::toUpperCase).toList())
                .status(NOT_STARTED)
                .build();

        ProjectEntity project = story.getProject();
        project.setTasks(project.getTasks() + 1L);

        taskRepository.save(builtTask);
        projectRepository.save(project);
        return mapper.map(builtTask, TaskDto.class);
    }

    @Override
    public List<MemberDto> getMembers(Long projectId, Long userId) throws FakeMemberException, UserNotFoundException {
        log.info("getMembers(): projectId={}, userId={}", projectId, userId);

        UserEntity user = userService.findUserById(userId);
        ProjectEntity project = findProject(projectId, user);

        return project.getUsers().stream()
                .map(userEntity -> new MemberDto(
                        userEntity.getId(),
                        "%s %s".formatted(userEntity.getFirstName(), userEntity.getLastName()),
                        userEntity.getLogin(),
                        userEntity.getRoles().stream().map(RoleEntity::getName).toList()
                )).toList();
    }

    @Override
    public void addMember(Long projectId, Long newUserId, Long userId) throws FakeMemberException, UserNotFoundException {
        log.info("addMember(): projectId={}, newUserId={}, userId={}", projectId, newUserId, userId);

        UserEntity user = userService.findUserById(userId);
        ProjectEntity project = findProject(projectId, user);

        UserEntity newMember = userService.findUserById(newUserId);
        boolean isAlreadyMember = newMember.getProjects().stream().anyMatch(project::equals);

        if (isAlreadyMember) {
            return;
        }

        project.addMember(newMember);
        projectRepository.save(project);
    }

    @Override
    public void removeMember(Long projectId, Long memberId, Long userId) throws FakeMemberException, UserNotFoundException {
        log.info("removeMember(): projectId={}, memberId={}, userId={}", projectId, memberId, userId);

        UserEntity user = userService.findUserById(userId);
        ProjectEntity project = findProject(projectId, user);

        UserEntity member = userService.findUserById(memberId);
        project.removeMember(member);

        projectRepository.save(project);
    }

    @Override
    public void assignTask(Long storyId, Long taskId, Long userId)
            throws FakeMemberException, TaskNotFoundException, UserNotFoundException, StoryNotFoundException {

        log.info("assignTask(): storyId={}, taskId={}, userId={}", storyId, taskId, userId);

        UserEntity user = userService.findUserById(userId);
        StoryEntity story = storyRepository.findById(storyId)
                .orElseThrow(() -> new StoryNotFoundException("story not found"));

        checkProjectMembership(user, story.getProject());

        TaskEntity task = findTask(story, taskId);
        task.setAssignedUser(user);
        taskRepository.save(task);
    }

    @Override
    public void changeTaskStatus(Long storyId, Long taskId, TaskStatus newStatus, Long userId)
            throws FakeMemberException, UserNotFoundException, StoryNotFoundException, TaskNotFoundException {

        log.info("changeTaskStatus(): storyId={}, taskId={}, userId={}", storyId, taskId, userId);

        UserEntity user = userService.findUserById(userId);
        StoryEntity story = storyRepository.findById(storyId)
                .orElseThrow(() -> new StoryNotFoundException("story not found"));

        ProjectEntity project = story.getProject();
        checkProjectMembership(user, project);

        TaskEntity task = findTask(story, taskId);
        Long updatedDoneTasks = project.getTasksDone();

        if (task.getStatus().equals(DONE) && !newStatus.equals(DONE)) { // DONE -> [NOT_STARTED, IN_PROGRESS]
            updatedDoneTasks = project.getTasksDone() - 1;
        } else if (!task.getStatus().equals(DONE) && newStatus.equals(DONE)) { // [NOT_STARTED, IN_PROGRESS] -> DONE
            updatedDoneTasks = project.getTasksDone() + 1;
        }

        project.setTasksDone(updatedDoneTasks);
        task.setStatus(newStatus);
        taskRepository.save(task);
        projectRepository.save(project);
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

    private StoryDto extractDto(StoryEntity story) {
        StoryDto mappedDto = mapper.map(story, StoryDto.class);
        mappedDto.setTasksCount(story.getTasks().size());
        return mappedDto;
    }

    private ProjectEntity findProject(Long projectId, UserEntity user) throws FakeMemberException {
        return user.getProjects().stream()
                .filter(project -> project.getId().equals(projectId))
                .findAny()
                .orElseThrow(() -> new FakeMemberException("you are not a member of the project"));
    }

    private TaskEntity findTask(StoryEntity story, Long taskId) throws TaskNotFoundException {
        return story.getTasks().stream()
                .filter(task -> task.getId().equals(taskId))
                .findAny()
                .orElseThrow(() -> new TaskNotFoundException("task is not found"));
    }

    private void checkProjectMembership(UserEntity user, ProjectEntity project) throws FakeMemberException {
        user.getProjects().stream()
                .filter(project::equals)
                .findAny()
                .orElseThrow(() -> new FakeMemberException("you are not member of the project"));
    }

}