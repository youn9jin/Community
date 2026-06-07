package com.example.community.auth.dto;

import com.example.community.user.dto.UserInfoResponseDTO;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class LoginResponseDTO {
    private final UserInfoResponseDTO user;
    private final String accessToken;
    private final String refreshToken;
}
