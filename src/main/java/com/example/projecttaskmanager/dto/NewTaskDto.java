package com.example.projecttaskmanager.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
public class NewTaskDto {

    @NotNull private long storyId;
    @NotNull private String title;
    @NotNull private List<String> marks;

}