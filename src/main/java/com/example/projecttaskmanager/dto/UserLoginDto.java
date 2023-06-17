package com.example.projecttaskmanager.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UserLoginDto {

    @NotBlank(message = "should not be blank")
    @Size(min = 3, max = 64, message = "length should be between 3 and 64")
    private final String login;

    @NotBlank(message = "should not be blank")
    @Size(min = 3, max = 64, message = "length should be between 3 and 64")
    private final String password;

}