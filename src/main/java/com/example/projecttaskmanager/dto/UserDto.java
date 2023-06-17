package com.example.projecttaskmanager.dto;

import lombok.*;

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

}