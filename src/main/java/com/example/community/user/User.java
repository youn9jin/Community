package com.example.community.user;

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
    private Long userId;

    @Column(unique = true)
    @Setter private String email;

    @Column(unique = true)
    @Setter private String nickname;
    @Setter private String password;

    @Enumerated(EnumType.STRING)
    @Setter private UserStatus status; //soft-delete 구현을 위한 UserStatus

    private LocalDateTime createdAt;
    private LocalDateTime deletedAt;

    @Setter
    @Column(name = "profile_image_id", unique = true)
    private Long profileImageId;

    public User(String email, String nickname, String password, UserStatus status, LocalDateTime createdAt, LocalDateTime deletedAt, Long profileImageId) {
        this.email = email;
        this.nickname = nickname;
        this.password = password;
        this.status = status;
        this.createdAt = createdAt;
        this.deletedAt = deletedAt;
        this.profileImageId = profileImageId;
    }
}
