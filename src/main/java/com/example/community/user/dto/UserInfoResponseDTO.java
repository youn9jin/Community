package com.example.community.user.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UserInfoResponseDTO {
    private  final long userId;
    private final String email;
    private final String nickname;
    private final Long profileImageId;
}
