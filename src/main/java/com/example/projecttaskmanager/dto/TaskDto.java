package com.example.projecttaskmanager.dto;

import com.example.projecttaskmanager.entity.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class TaskDto {
    private Long id;
    private String title;
    private TaskStatus status;
    private Long assignedUserId;
    private String assignedFullName;
    private List<String> marks;
}