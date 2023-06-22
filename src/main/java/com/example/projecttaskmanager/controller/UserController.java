package com.example.projecttaskmanager.controller;

import com.example.projecttaskmanager.dto.AvatarDto;
import com.example.projecttaskmanager.dto.UserInfoDto;
import com.example.projecttaskmanager.dto.UserUpdateDto;
import com.example.projecttaskmanager.exception.UserNotFoundException;
import com.example.projecttaskmanager.security.UserDetailsImpl;
import com.example.projecttaskmanager.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.NO_CONTENT;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PatchMapping("/logout")
    @ResponseStatus(NO_CONTENT)
    public void logout() throws UserNotFoundException {
        userService.logout(getPrincipal().getId());
    }

    @GetMapping
    public UserInfoDto getUserInfo() throws UserNotFoundException {
        return userService.getUserInfo(getPrincipal().getId());
    }

    @ResponseStatus(NO_CONTENT)
    @PatchMapping("/update")
    public void updateUserInfo(@RequestBody UserUpdateDto dto) throws UserNotFoundException {
        userService.updateUser(dto, getPrincipal().getId());
    }

    @ResponseStatus(NO_CONTENT)
    @PostMapping("/avatar")
    public void updateAvatar(@RequestBody AvatarDto dto) throws UserNotFoundException {
        userService.updateAvatar(dto, getPrincipal().getId());
    }

    @GetMapping("/avatar/{id}")
    public AvatarDto getAvatar(@PathVariable Long id) throws UserNotFoundException {
        return userService.getAvatar(id);
    }

    private UserDetailsImpl getPrincipal() {
        return (UserDetailsImpl) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
    }

}
