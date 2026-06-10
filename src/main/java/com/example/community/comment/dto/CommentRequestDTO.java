package com.example.community.comment.dto;

import com.example.community.user.dto.UserInfoResponseDTO;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class CommentRequestDTO {
    @NotBlank
    private final String content;
}
