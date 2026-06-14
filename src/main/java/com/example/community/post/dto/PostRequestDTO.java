package com.example.community.post.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PostRequestDTO {
    @NotBlank
    private final String title;

    @NotBlank
    private final String content;

    private final Integer imageId;

    private final boolean removeImage;
}
