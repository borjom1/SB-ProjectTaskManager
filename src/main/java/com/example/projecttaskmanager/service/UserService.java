package com.example.projecttaskmanager.service;

import com.example.projecttaskmanager.dto.*;
import com.example.projecttaskmanager.entity.UserEntity;
import com.example.projecttaskmanager.exception.CredentialsNotMatchException;
import com.example.projecttaskmanager.exception.LoginAlreadyExistsException;
import com.example.projecttaskmanager.exception.UserNotFoundException;
import org.apache.catalina.User;

public interface UserService {

    UserDto register(UserRegistrationDto dto) throws LoginAlreadyExistsException;
    UserDto login(UserLoginDto dto) throws CredentialsNotMatchException;
    UserDto refreshTokens(TokenDto dto) throws CredentialsNotMatchException;
    void logout(Long userId) throws UserNotFoundException;
    UserInfoDto getUserInfo(Long userId) throws UserNotFoundException;
    UserEntity findUserById(Long userId) throws UserNotFoundException;
    void updateUser(UserUpdateDto dto, Long userId) throws UserNotFoundException;
    void updateAvatar(AvatarDto dto, Long userId) throws UserNotFoundException;
    AvatarDto getAvatar(Long id) throws UserNotFoundException;

}