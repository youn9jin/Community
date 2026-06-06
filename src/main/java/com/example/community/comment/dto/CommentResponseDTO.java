package com.example.community.comment.dto;

import com.example.community.user.dto.UserInfoResponseDTO;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class CommentResponseDTO {
    private final Integer commentId;
    private final UserInfoResponseDTO user;
    private final LocalDateTime createdAt;
    private final String commentContent;
}
