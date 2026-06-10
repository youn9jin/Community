package com.example.community.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PasswordChangeRequestDTO {

    @NotBlank
    private final String currentPassword;

    @NotBlank
    private final String newPassword;

}
