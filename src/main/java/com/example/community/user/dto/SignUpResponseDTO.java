package com.example.community.user.dto;

import com.example.community.user.UserStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SignUpResponseDTO {
    private final Integer userId;
    private final String email;
    private final String nickname;
    private final UserStatus status;
    private final String profileImgUrl;
}
