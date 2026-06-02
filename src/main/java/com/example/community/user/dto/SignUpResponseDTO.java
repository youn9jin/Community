package com.example.community.user.dto;

import com.example.community.user.UserStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignUpResponseDTO {
    private long userId;
    private String email;
    private String nickname;
    private String profileImageUrl;
    private UserStatus status;
}
