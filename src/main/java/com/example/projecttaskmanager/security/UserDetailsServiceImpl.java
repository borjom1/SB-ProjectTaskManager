package com.example.projecttaskmanager.security;

import com.example.projecttaskmanager.entity.UserEntity;
import com.example.projecttaskmanager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = userRepository.findUserEntityByLogin(username)
                .orElseThrow(() -> new UsernameNotFoundException("user with username %s was not found".formatted(username)));
        return new UserDetailsImpl(user.getId(), user.getLogin(), user.getPassword(), user.getRoles());
    }
}
