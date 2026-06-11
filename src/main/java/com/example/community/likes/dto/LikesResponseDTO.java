package com.example.community.likes.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class LikesResponseDTO {
    private final Integer postId;
    private final long likeCount;
}
