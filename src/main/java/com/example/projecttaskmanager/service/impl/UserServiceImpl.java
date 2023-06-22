package com.example.projecttaskmanager.service.impl;

import com.example.projecttaskmanager.dto.*;
import com.example.projecttaskmanager.entity.RoleEntity;
import com.example.projecttaskmanager.entity.UserEntity;
import com.example.projecttaskmanager.exception.CredentialsNotMatchException;
import com.example.projecttaskmanager.exception.LoginAlreadyExistsException;
import com.example.projecttaskmanager.exception.UserNotFoundException;
import com.example.projecttaskmanager.model.Tokens;
import com.example.projecttaskmanager.repository.RoleRepository;
import com.example.projecttaskmanager.repository.UserRepository;
import com.example.projecttaskmanager.security.jwt.JwtProvider;
import com.example.projecttaskmanager.service.UserService;
import com.example.projecttaskmanager.util.AvatarUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalDate;
import java.time.ZoneId;
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
            throw new LoginAlreadyExistsException("such login already exists");
        }

        UserEntity newUser = mapper.map(dto, UserEntity.class);
        RoleEntity roleUser = roleRepository.findRoleEntityByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("role <USER> is not present in DB"));

        newUser.setPassword(encoder.encode(dto.getPassword()));
        newUser.addRole(roleUser);
        userRepository.save(newUser);

        return packUserDto(newUser);
    }

    @Override
    public UserDto login(UserLoginDto dto) throws CredentialsNotMatchException {
        log.info("login(): {}", dto.getLogin());

        UserEntity user = userRepository.findUserEntityByLogin(dto.getLogin())
                .orElseThrow(() -> new CredentialsNotMatchException("user with login <%s> does not exist".formatted(dto.getLogin())));

        if (!encoder.matches(dto.getPassword(), user.getPassword())) {
            throw new CredentialsNotMatchException("incorrect credentials");
        }

        return packUserDto(user);
    }

    @Override
    public UserDto refreshTokens(TokenDto dto) throws CredentialsNotMatchException {
        log.info("refreshTokens():");

        String refreshToken = dto.getToken();
        if (!jwtProvider.isTokenValid(refreshToken, REFRESH)) {
            throw new CredentialsNotMatchException("refresh token is invalid");
        }
        UserEntity user = userRepository.findUserEntityByRefreshToken(refreshToken)
                .orElseThrow(() -> new CredentialsNotMatchException("refresh token not match"));

        return packUserDto(user);
    }

    @Override
    public void logout(Long userId) throws UserNotFoundException {
        log.info("logout(): user-id={}", userId);

        UserEntity user = findUserById(userId);
        user.setRefreshToken(null);
        userRepository.save(user);
    }

    @Override
    public UserInfoDto getUserInfo(Long userId) throws UserNotFoundException {
        log.info("getUserInfo(): user-id={}", userId);

        UserEntity user = findUserById(userId);
        return new UserInfoDto(
                "%s %s".formatted(user.getFirstName(), user.getLastName()),
                user.getPosition(),
                LocalDate.ofInstant(user.getCreatedAt(), ZoneId.systemDefault()),
                user.getProjectsCount(),
                user.getRoles().stream()
                        .map(RoleEntity::getName)
                        .map(role -> role.substring(5))
                        .toList()
        );
    }

    @Override
    public UserEntity findUserById(Long userId) throws UserNotFoundException {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("user with specified id not found"));
    }

    @Override
    public void updateUser(UserUpdateDto dto, Long userId) throws UserNotFoundException {
        log.info("updateUser(): user-id={}, dto={}", userId, dto);
        UserEntity user = findUserById(userId);

        user.setFirstName(dto.getFirstName() != null ? dto.getFirstName() : user.getFirstName());
        user.setLastName(dto.getLastName() != null ? dto.getLastName() : user.getLastName());
        user.setPosition(dto.getPosition() != null ? dto.getPosition() : user.getPosition());

        userRepository.save(user);
    }

    @Override
    public void updateAvatar(AvatarDto dto, Long userId) throws UserNotFoundException {
        log.info("updateAvatar(): user-id={}", userId);

        UserEntity user = findUserById(userId);
        AvatarUtils.saveFile(String.valueOf(user.getId()), dto.getBinary());
        user.setAvatar("%s%s%d".formatted(AvatarUtils.AVATARS_DIR_PATH, File.separator, user.getId()));

        userRepository.save(user);
    }

    @Override
    public AvatarDto getAvatar(Long id) throws UserNotFoundException {
        log.info("getAvatar(): id={}", id);
        UserEntity user = findUserById(id);

        String content = AvatarUtils.readFile(user.getAvatar());
        return new AvatarDto(content);
    }

    private UserDto packUserDto(UserEntity user) {
        Tokens tokens = generateTokens(user.getId(), user.getLogin());
        user.setRefreshToken(tokens.refresh());
        userRepository.save(user);

        UserDto mappedDto = mapper.map(user, UserDto.class);
        mappedDto.setRefresh(tokens.refresh());
        mappedDto.setAccess(tokens.access());

        return mappedDto;
    }

    private Tokens generateTokens(Long id, String login) {
        return new Tokens(
                jwtProvider.generateToken(ACCESS, id, login),
                jwtProvider.generateToken(REFRESH, id, login)
        );
    }

}