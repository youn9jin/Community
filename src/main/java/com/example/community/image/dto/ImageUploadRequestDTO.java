package com.example.community.image.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Getter
@RequiredArgsConstructor
public class ImageUploadRequestDTO {

    @NotNull
    private final MultipartFile file;

    //파일 크기 제한
    public static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
}
