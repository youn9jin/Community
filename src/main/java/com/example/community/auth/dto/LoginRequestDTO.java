package com.example.community.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class LoginRequestDTO {

    @Email
    @NotEmpty
    private final String email;

    @NotEmpty
    private final String password;
}
