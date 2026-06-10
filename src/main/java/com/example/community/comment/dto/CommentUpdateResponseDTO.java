package com.example.community.comment.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CommentUpdateResponseDTO {
    private final String content;
}