package com.example.community.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SignUpRequestDTO {

    @NotEmpty
    @Email
    private final String email;

    @NotEmpty
    @Size(min = 3, max = 25)
    private final String nickname;

    @NotEmpty
    private final String password;
}
