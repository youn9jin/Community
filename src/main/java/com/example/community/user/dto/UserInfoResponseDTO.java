package com.example.community.user.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UserInfoResponseDTO {
    private  final Integer userId;
    private final String email;
    private final String nickname;
}
