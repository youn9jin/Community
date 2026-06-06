package com.example.community.post.dto;

import com.example.community.user.dto.UserInfoResponseDTO;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class PostListResposneDTO {
    private final Integer postId;
    private final String title;
    private final LocalDateTime createdAt;
    private final UserInfoResponseDTO user;
    private final LocalDateTime updatedAt;
    private final int viewCount;
    private final int likeCount;
    private final int commentCount;
}
