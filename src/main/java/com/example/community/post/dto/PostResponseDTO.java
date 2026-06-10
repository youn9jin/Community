package com.example.community.post.dto;

import com.example.community.user.dto.WriterDTO;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class PostResponseDTO {
    private final Integer postId;
    private final String title;
    private final WriterDTO writer;
    private final String content;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
}
