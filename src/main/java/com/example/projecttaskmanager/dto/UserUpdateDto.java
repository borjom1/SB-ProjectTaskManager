package com.example.projecttaskmanager.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public class UserUpdateDto {
    private String firstName;
    private String lastName;
    private String position;
}