package com.example.community.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SignUpRequestDTO {

    @NotBlank
    @Email
    private final String email;

    @NotBlank
    @Size(min = 3, max = 25)
    private final String nickname;

    @NotBlank
    private final String password;
}
