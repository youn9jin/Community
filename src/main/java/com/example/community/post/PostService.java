package com.example.community.post;

import com.example.community.post.dto.PostDetailResponseDTO;
import com.example.community.post.dto.PostRequestDTO;
import com.example.community.post.dto.PostResponseDTO;
import com.example.community.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository repository;

    Page<PostListResponseDTO> getPosts(Pageable pageable){

    };

    PostDetailResponseDTO getPost(Integer postId){

    };

    PostResponseDTO createPost(Integer userId, PostRequestDTO request){

    };

    PostResponseDTO updatePost(Integer postId, Integer userId, PostRequestDTO request){

    };

    void deletePost(Integer postId, Integer userId){

    };

}
