package com.example.projecttaskmanager.service.impl;

import com.example.projecttaskmanager.dto.UserDto;
import com.example.projecttaskmanager.dto.UserLoginDto;
import com.example.projecttaskmanager.dto.UserRegistrationDto;
import com.example.projecttaskmanager.entity.RoleEntity;
import com.example.projecttaskmanager.entity.UserEntity;
import com.example.projecttaskmanager.exception.CredentialsNotMatchException;
import com.example.projecttaskmanager.exception.LoginAlreadyExistsException;
import com.example.projecttaskmanager.repository.RoleRepository;
import com.example.projecttaskmanager.repository.UserRepository;
import com.example.projecttaskmanager.security.jwt.JwtProvider;
import com.example.projecttaskmanager.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.example.projecttaskmanager.security.jwt.JwtType.ACCESS;
import static com.example.projecttaskmanager.security.jwt.JwtType.REFRESH;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final ModelMapper mapper;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder encoder;

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    public UserDto register(UserRegistrationDto dto) throws LoginAlreadyExistsException {
        log.info("register(): {}", dto);

        Optional<UserEntity> user = userRepository.findUserEntityByLogin(dto.getLogin());

        if (user.isPresent()) {
            throw new LoginAlreadyExistsException("user with login <%s> already exists".formatted(dto.getLogin()));
        }

        UserEntity newUser = mapper.map(dto, UserEntity.class);
        RoleEntity roleUser = roleRepository.findRoleEntityByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("role <USER> is not present in DB"));

        newUser.setPassword(encoder.encode(dto.getPassword()));
        newUser.addRole(roleUser);
        userRepository.save(newUser);

        UserDto mappedDto = mapper.map(newUser, UserDto.class);
        mappedDto.setRoles(newUser.getRoles().stream().map(RoleEntity::getName).toList());
        return generateTokens(mappedDto);
    }

    @Override
    public UserDto login(UserLoginDto dto) throws CredentialsNotMatchException {
        log.info("login(): {}", dto.getLogin());

        UserEntity user = userRepository.findUserEntityByLogin(dto.getLogin())
                .orElseThrow(() -> new CredentialsNotMatchException("user with login <%s> does not exist".formatted(dto.getLogin())));

        if (!encoder.matches(dto.getPassword(), user.getPassword())) {
            throw new CredentialsNotMatchException("incorrect credentials");
        }

        UserDto mappedDto = mapper.map(user, UserDto.class);
        mappedDto.setRoles(user.getRoles().stream().map(RoleEntity::getName).toList());
        return generateTokens(mappedDto);
    }

    private UserDto generateTokens(UserDto dto) {
        dto.setAccess(jwtProvider.generateToken(ACCESS, dto.getId(), dto.getLogin()));
        dto.setRefresh(jwtProvider.generateToken(REFRESH, dto.getId(), dto.getLogin()));
        return dto;
    }

}