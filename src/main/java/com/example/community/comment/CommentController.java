package com.example.community.comment;

import com.example.community.comment.dto.CommentRequestDTO;
import com.example.community.comment.dto.CommentResponseDTO;
import com.example.community.comment.dto.CommentUpdateResponseDTO;
import com.example.community.global.ResponseWrapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts/{postId}/comments")
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<ResponseWrapper<CommentResponseDTO>> createComment(
            @PathVariable Integer postId,
            @AuthenticationPrincipal Integer userId, // 인증 과정
            @Valid @RequestBody CommentRequestDTO request) {

        CommentResponseDTO response = commentService.createComment(postId, userId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseWrapper.success("comment uploading success", response));
    }

    @PatchMapping("/{commentId}")
    public ResponseEntity<ResponseWrapper<CommentUpdateResponseDTO>> updateComment(
            @PathVariable Integer postId,
            @PathVariable Integer commentId,
            @AuthenticationPrincipal Integer userId,
            @Valid @RequestBody CommentRequestDTO request) {

        CommentUpdateResponseDTO response = commentService.updateComment(postId, commentId, userId, request);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseWrapper.success("Modified complete", response));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Integer postId,
            @PathVariable Integer commentId,
            @AuthenticationPrincipal Integer userId) {
        commentService.deleteComment(postId, commentId, userId);
        return ResponseEntity.noContent().build();
    }
}
