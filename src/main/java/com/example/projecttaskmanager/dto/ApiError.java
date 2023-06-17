package com.example.projecttaskmanager.dto;

import java.time.ZonedDateTime;

public record ApiError(String path, String message, int status, ZonedDateTime timestamp) {
}