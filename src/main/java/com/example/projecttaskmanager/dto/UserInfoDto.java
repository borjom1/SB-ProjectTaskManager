package com.example.projecttaskmanager.dto;

import java.time.LocalDate;
import java.util.List;

public record UserInfoDto(
        String fullName,
        String position,
        LocalDate joined,
        long projects,
        List<String> roles) {
}