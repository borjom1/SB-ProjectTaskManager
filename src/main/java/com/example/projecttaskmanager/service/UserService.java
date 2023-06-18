package com.example.projecttaskmanager.service;

import com.example.projecttaskmanager.dto.TokenDto;
import com.example.projecttaskmanager.dto.UserDto;
import com.example.projecttaskmanager.dto.UserLoginDto;
import com.example.projecttaskmanager.dto.UserRegistrationDto;
import com.example.projecttaskmanager.exception.CredentialsNotMatchException;
import com.example.projecttaskmanager.exception.LoginAlreadyExistsException;
import com.example.projecttaskmanager.exception.UserNotFoundException;

public interface UserService {

    UserDto register(UserRegistrationDto dto) throws LoginAlreadyExistsException;
    UserDto login(UserLoginDto dto) throws CredentialsNotMatchException;
    UserDto refreshTokens(TokenDto dto) throws CredentialsNotMatchException;
    void logout(Long userId) throws UserNotFoundException;

}