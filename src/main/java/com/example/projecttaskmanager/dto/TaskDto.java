package com.example.projecttaskmanager.dto;

import com.example.projecttaskmanager.entity.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TaskDto {
    private Long id;
    private String title;
    private TaskStatus status;
    private Long assignedUserId;
    private String assignedFullName;
    private List<String> marks;
}