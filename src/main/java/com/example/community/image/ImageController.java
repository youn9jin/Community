package com.example.community.image;

import com.example.community.global.ResponseWrapper;
import com.example.community.image.dto.ImageUploadResponseDTO;
import com.example.community.image.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/images")
public class ImageController {

    private final ImageService imageService;

    @PostMapping("/posts")
    public ResponseEntity<ResponseWrapper<ImageUploadResponseDTO>> uploadPostImage(
            @RequestPart("file") MultipartFile file,
            @AuthenticationPrincipal Integer loginUserId) {

        ImageUploadResponseDTO response = imageService.uploadPostImage(file, loginUserId);
        return ResponseEntity.ok(ResponseWrapper.success("image upload success", response));
    }

    @PostMapping("/profile")
    public ResponseEntity<ResponseWrapper<ImageUploadResponseDTO>> uploadProfileImage(
            @RequestPart("file") MultipartFile file,
            @AuthenticationPrincipal Integer loginUserId) {

        ImageUploadResponseDTO response = imageService.uploadProfileImage(file, loginUserId);
        return ResponseEntity.ok(ResponseWrapper.success("image upload success", response));
    }

    @DeleteMapping("/{imageId}")
    public ResponseEntity<Void> deleteImage(
            @PathVariable Integer imageId,
            @AuthenticationPrincipal Integer loginUserId) {

        imageService.deleteImage(imageId, loginUserId);
        return ResponseEntity.noContent().build();
    }

}
