package com.example.projecttaskmanager.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
public class NewStoryDto {
    @NotNull private String name;
    @NotNull private LocalDate from;
    @NotNull private LocalDate to;
}