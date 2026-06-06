package com.example.community.user;

import com.example.community.user.UserStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userId;

    @Column(unique = true)
    @Setter private String email;

    @Column(unique = true)
    @Setter
    private String nickname;

    @Setter private String password;

    @Enumerated(EnumType.STRING)
    @Setter private UserStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime deletedAt;

    public User(String email, String nickname, String password) {
        this.email = email;
        this.nickname = nickname;
        this.password = password;
        this.status = UserStatus.ACTIVE;
        this.createdAt = LocalDateTime.now();
    }
}