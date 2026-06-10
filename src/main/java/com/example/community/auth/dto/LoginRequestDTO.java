package com.example.community.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class LoginRequestDTO {

    @Email
    @NotBlank
    private final String email;

    @NotBlank
    private final String password;
}
