package com.example.community.post.repository;

import com.example.community.post.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostRepositoryCustom {
    Page<Post> findActivePosts(Pageable pageable);
}