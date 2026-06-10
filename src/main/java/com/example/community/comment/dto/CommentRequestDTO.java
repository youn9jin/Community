package com.example.community.comment.dto;

import com.example.community.user.dto.UserInfoResponseDTO;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class CommentRequestDTO {
    @NotEmpty
    private final String content;
}
