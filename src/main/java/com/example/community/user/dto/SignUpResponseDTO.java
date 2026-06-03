package com.example.community.user.dto;

import com.example.community.user.UserStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@RequiredArgsConstructor
public class SignUpResponseDTO {
    private final Long userId;
    private final String email;
    private final String nickname;
    private final String profileImageUrl;
    private final UserStatus status;
}
