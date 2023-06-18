package com.example.projecttaskmanager.controller;

import com.example.projecttaskmanager.exception.UserNotFoundException;
import com.example.projecttaskmanager.security.UserDetailsImpl;
import com.example.projecttaskmanager.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.NO_CONTENT;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PatchMapping("/logout")
    @ResponseStatus(NO_CONTENT)
    public void logout() throws UserNotFoundException {
        var principal = (UserDetailsImpl) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        userService.logout(principal.getId());
    }

}
