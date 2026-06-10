package com.example.community.user.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UpdateUserRequestDTO {

    @Size(min = 3, max = 25)
    private final String nickname;

}
