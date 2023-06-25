package com.example.projecttaskmanager.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MemberDto {

    private Long id;
    private String fullName;
    private String login;
    private List<String> roles;

}