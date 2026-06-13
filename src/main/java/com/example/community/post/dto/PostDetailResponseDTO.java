package com.example.community.post.dto;

import com.example.community.comment.dto.CommentResponseDTO;
import com.example.community.user.dto.WriterDTO;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class PostDetailResponseDTO {
    private final Integer postId;
    private final String title;
    private final LocalDateTime createdAt;
    private final WriterDTO writer;
    private final LocalDateTime updatedAt;
    private final int viewCount;
    private final int likeCount;
    private final String content;
    private final List<String> imageUrls;
    private final List<CommentResponseDTO> comments;
}
