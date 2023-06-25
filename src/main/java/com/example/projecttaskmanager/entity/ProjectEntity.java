package com.example.projecttaskmanager.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "projects")
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(of = {"id", "name"})
@JsonIgnoreProperties("users")
@ToString(exclude = {"users", "stories"})
@Builder
public class ProjectEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;

    @Builder.Default
    private Long tasks = 0L;

    @Builder.Default
    private Long tasksDone = 0L;

    @ManyToMany
    @JoinTable(
            name = "members",
            joinColumns = @JoinColumn(name = "project_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @Builder.Default
    private List<UserEntity> users = new ArrayList<>();

    @OneToMany(mappedBy = "project")
    @Builder.Default
    private List<StoryEntity> stories = new ArrayList<>();

    public void addMember(UserEntity user) {
        users.add(user);
        user.getProjects().add(this);
        user.setProjectsCount(user.getProjectsCount() + 1L);
    }

    public void removeMember(UserEntity user) {
        users.remove(user);
        user.getProjects().remove(this);
        user.setProjectsCount(user.getProjectsCount() - 1L);
    }

    public void addStory(StoryEntity story) {
        this.stories.add(story);
        story.setProject(this);
    }

}