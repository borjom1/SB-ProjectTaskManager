package com.example.projecttaskmanager.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tasks")
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(of = "id")
@JsonIgnoreProperties({"story", "assignedUser"})
@ToString(exclude = {"story", "assignedUser"})
@Builder
public class TaskEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "story_id")
    private StoryEntity story;

    @ManyToOne(optional = false)
    @JoinColumn(name = "assigned_user_id")
    private UserEntity assignedUser;

    private String title;

    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    @ElementCollection(targetClass = String.class)
    @CollectionTable(
            name = "task_marks",
            joinColumns = @JoinColumn(name = "task_id")
    )
    @Column(name = "mark", nullable = false)
    @Builder.Default
    private List<String> marks = new ArrayList<>();

}