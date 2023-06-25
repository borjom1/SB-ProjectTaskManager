package com.example.projecttaskmanager.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "stories")
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(of = "id")
@JsonIgnoreProperties("project")
@ToString(exclude = "project")
@Builder
public class StoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "project_id")
    private ProjectEntity project;
    private String name;

    @Column(name = "start_date")
    private LocalDate start;

    @Column(name = "end_date")
    private LocalDate end;

    @Builder.Default
    private Instant createdAt = Instant.now();

    @OneToMany(mappedBy = "story")
    @Builder.Default
    private List<TaskEntity> tasks = new ArrayList<>();

    public void addTask(TaskEntity task) {
        task.setStory(this);
        tasks.add(task);
    }

}