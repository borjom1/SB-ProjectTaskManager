package com.example.projecttaskmanager.dto;

import java.time.ZonedDateTime;

public record ApiError(String path, String error, int status, ZonedDateTime timestamp) {
}