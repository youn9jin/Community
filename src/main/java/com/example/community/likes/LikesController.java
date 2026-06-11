package com.example.community.likes;

import com.example.community.global.ResponseWrapper;
import com.example.community.likes.dto.LikesResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts/{postId}/likes")
public class LikesController {

    private final LikesService likesService;

    @PostMapping
    public ResponseEntity<ResponseWrapper<LikesResponseDTO>> addLike(
            @PathVariable Integer postId,
            @AuthenticationPrincipal Integer loginUserId
    ){
        LikesResponseDTO response = likesService.addLike(postId, loginUserId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseWrapper.success("likes upload success", response));
    }

    @DeleteMapping
    public ResponseEntity<Void> removeLike(
            @PathVariable Integer postId,
            @AuthenticationPrincipal Integer loginUserId
    ){
        likesService.removeLike(postId, loginUserId);
        return ResponseEntity.noContent().build(); //204 응답

    }
}
