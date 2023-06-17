package com.example.projecttaskmanager.exception;

public class LoginAlreadyExistsException extends Exception {
    public LoginAlreadyExistsException(String message) {
        super(message);
    }
}