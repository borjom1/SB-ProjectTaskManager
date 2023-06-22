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
@ToString(exclude = "users")
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

    public void addMember(UserEntity user) {
        users.add(user);
        user.getProjects().add(this);
    }

}