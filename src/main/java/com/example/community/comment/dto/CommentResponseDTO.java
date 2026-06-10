package com.example.community.comment.dto;

import com.example.community.user.dto.WriterDTO;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class CommentResponseDTO {
    private final Integer commentId;
    private final WriterDTO writer;
    private final LocalDateTime createdAt;
    private final String content;
}
