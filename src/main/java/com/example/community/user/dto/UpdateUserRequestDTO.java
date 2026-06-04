package com.example.community.user.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UpdateUserRequestDTO {

    @Size(min = 3, max = 25, message = "닉네임은 3~25자 사이어야 합니다.")
    private final String nickname;

    private final Long profileImageId;

}
