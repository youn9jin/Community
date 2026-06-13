package com.example.community.image.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class ImageUploadResponseDTO {
    private final Integer imageId;
    private final String storagePath;
    private final String thumbnailPath;
    private final boolean isActive;
    private final LocalDateTime createdAt;
    private final Integer uploadedByUserId;
}
