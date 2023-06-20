package com.example.projecttaskmanager.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserDto {

    private long id;
    private String login;
    private String access;
    private String refresh;

}