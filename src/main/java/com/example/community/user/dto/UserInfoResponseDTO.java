package com.example.community.user.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UserInfoResponseDTO {
    private  final Integer userId;
    private final String email;
    private final String nickname;
    private final String profileImgUrl;
}
