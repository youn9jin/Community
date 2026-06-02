package com.example.community.user.dto;

import com.example.community.user.UserStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserInfoResponseDTO {
    private long userId;
    private String email;
    private String nickname;
    private String profileImageUrl;
}
