package com.example.projecttaskmanager.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString(exclude = {"password"})
public class UserRegistrationDto {

    @NotBlank(message = "should not be blank")
    @Size(min = 3, max = 64, message = "length should be between 3 and 64")
    private final String firstName;

    @NotBlank(message = "should not be blank")
    @Size(min = 3, max = 64, message = "length should be between 3 and 64")
    private final String lastName;

    @NotBlank(message = "should not be blank")
    @Size(min = 3, max = 64, message = "length should be between 3 and 64")
    private final String login;

    @NotBlank(message = "should not be blank")
    @Size(min = 3, max = 64, message = "length should be between 3 and 64")
    private final String position;

    @NotBlank(message = "should not be blank")
    @Size(min = 3, max = 64, message = "length should be between 3 and 64")
    private final String password;

}