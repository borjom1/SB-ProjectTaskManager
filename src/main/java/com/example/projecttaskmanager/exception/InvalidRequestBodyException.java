package com.example.projecttaskmanager.exception;

public class InvalidRequestBodyException extends Exception {
    public InvalidRequestBodyException(String message) {
        super(message);
    }
}