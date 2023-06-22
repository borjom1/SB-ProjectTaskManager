package com.example.projecttaskmanager.entity;

import com.example.projecttaskmanager.util.AvatarUtils;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(of = {"id", "login"})
@Builder
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;

    @Column(unique = true)
    private String login;

    private String password;

    @Builder.Default
    private Instant createdAt = Instant.now();

    @Builder.Default
    private String avatar = AvatarUtils.DEFAULT_AVATAR_PATH;

    private String position;

    @Column(name = "projects")
    @Builder.Default
    private long projectsCount = 0;

    @Builder.Default
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private List<RoleEntity> roles = new ArrayList<>();

    private String refreshToken;

    @Builder.Default
    @ManyToMany(mappedBy = "users")
    private List<ProjectEntity> projects = new ArrayList<>();

    public void addRole(RoleEntity role) {
        this.roles.add(role);
        role.getUsers().add(this);
    }

}