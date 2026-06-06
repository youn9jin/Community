package com.example.community.post.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PostRequestDTO {
    private final String title;
    private final String content;
}
