package com.example.projecttaskmanager.controller;

import com.example.projecttaskmanager.dto.UserDto;
import com.example.projecttaskmanager.dto.UserLoginDto;
import com.example.projecttaskmanager.dto.UserRegistrationDto;
import com.example.projecttaskmanager.exception.CredentialsNotMatchException;
import com.example.projecttaskmanager.exception.LoginAlreadyExistsException;
import com.example.projecttaskmanager.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.OK;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    @ResponseStatus(OK)
    public UserDto register(@Valid @RequestBody UserRegistrationDto dto) throws LoginAlreadyExistsException {
        return userService.register(dto);
    }

    @PostMapping("/login")
    @ResponseStatus(OK)
    public UserDto login(@Valid @RequestBody UserLoginDto dto) throws CredentialsNotMatchException {
        return userService.login(dto);
    }

}