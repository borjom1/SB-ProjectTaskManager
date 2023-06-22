package com.example.projecttaskmanager.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor(force = true)
@AllArgsConstructor
@Getter
@ToString
public class NewProjectDto {

    @NotBlank(message = "should not be blank")
    private final String name;

    @NotBlank(message = "should not be blank")
    private final String description;

}