package com.example.community.user.dto;

import com.example.community.user.UserStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@RequiredArgsConstructor
public class UserInfoResponseDTO {
    private  final long userId;
    private final String email;
    private final String nickname;
    private final String profileImageUrl;
}
