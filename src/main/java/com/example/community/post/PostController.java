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
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {
    private final PostService postService;

    @GetMapping
    public ResponseEntity<ResponseWrapper<Page<PostListResposneDTO>>> getPosts(Pageable pageable) {
        Page<PostListResposneDTO> response = postService.getPosts(pageable);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseWrapper.success("post list load completed", response));
    }

    @GetMapping("/{postId}")
    public ResponseEntity<ResponseWrapper<PostDetailResponseDTO>> getPost(@PathVariable Integer postId) {
        PostDetailResponseDTO response = postService.getPost(postId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseWrapper.success("post detail load completed", response));
    }

    @PostMapping
    public ResponseEntity<ResponseWrapper<PostResponseDTO>> createPost(
            @RequestParam Integer userId,
            @Valid @RequestBody PostRequestDTO request) {

        PostResponseDTO response = postService.createPost(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseWrapper.success("post creation completed", response));
    }

    @PatchMapping("/{postId}")
    public ResponseEntity<ResponseWrapper<PostResponseDTO>> updatePost(
            @PathVariable Integer postId,
            @RequestParam Integer userId,
            @Valid @RequestBody PostRequestDTO request) {

        PostResponseDTO response = postService.updatePost(postId, userId, request);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseWrapper.success("post update completed", response));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<ResponseWrapper<Void>> deletePost(
            @PathVariable Integer postId,
            @RequestParam Integer userId) {

        postService.deletePost(postId, userId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseWrapper.success("post deletion completed", null));
    }
}
