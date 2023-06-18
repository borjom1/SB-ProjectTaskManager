package com.example.projecttaskmanager.dto;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserDto {

    private long id;
    private String firstName;
    private String lastName;
    private String login;
    private long projects;
    private String access;
    private String refresh;
    private List<String> roles;

}