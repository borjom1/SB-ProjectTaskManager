package com.example.projecttaskmanager.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ProjectDto {
    private Long id;
    private String name;
    private String description;
    private int members;
    private Long tasksCompleted;
    private Long tasksInProcess;
}