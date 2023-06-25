package com.example.projecttaskmanager.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class StoryDto {

    private Long id;
    private String name;
    private int tasksCount;
    private LocalDate start;
    private LocalDate end;

}