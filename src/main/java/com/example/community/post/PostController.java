package com.example.community.post;

import com.example.community.global.ResponseWrapper;
import com.example.community.post.dto.PostDetailResponseDTO;
import com.example.community.post.dto.PostListResposneDTO;
import com.example.community.post.dto.PostRequestDTO;
import com.example.community.post.dto.PostResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {
    private final PostService postService;

    // 게시글 목록 페이지 조회
    @GetMapping
    public ResponseEntity<ResponseWrapper<Page<PostListResposneDTO>>> getPosts(Pageable pageable) {
        Page<PostListResposneDTO> response = postService.getPosts(pageable);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseWrapper.success("post list load completed", response));
    }

    // postId에 해당하는 게시글 상세 조회
    @GetMapping("/{postId}")
    public ResponseEntity<ResponseWrapper<PostDetailResponseDTO>> getPost(@PathVariable Integer postId) {
        PostDetailResponseDTO response = postService.getPost(postId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseWrapper.success("load completed", response));
    }

    // 인증된 사용자의 userId로 새 게시글 작성
    @PostMapping
    public ResponseEntity<ResponseWrapper<PostResponseDTO>> createPost(
            @Valid @RequestBody PostRequestDTO request) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Integer userId = (Integer) authentication.getPrincipal();
        PostResponseDTO response = postService.createPost(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseWrapper.success("post uploading success", response));
    }

    // 인증된 사용자의 본인 게시글 수정
    @PatchMapping("/{postId}")
    public ResponseEntity<ResponseWrapper<PostResponseDTO>> updatePost(
            @PathVariable Integer postId,
            @Valid @RequestBody PostRequestDTO request) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Integer userId = (Integer) authentication.getPrincipal();
        PostResponseDTO response = postService.updatePost(postId, userId, request);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseWrapper.success("modified uploading success", response));
    }

    // 인증된 사용자의 본인 게시글 soft delete
    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(
            @PathVariable Integer postId) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Integer userId = (Integer) authentication.getPrincipal();
        postService.deletePost(postId, userId);
        return ResponseEntity.noContent().build(); //204 응답
    }
}
