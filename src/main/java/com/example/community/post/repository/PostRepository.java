package com.example.community.post.repository;

import com.example.community.post.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Integer>, PostRepositoryCustom {
    Optional<Post> findByIdAndDeletedAtIsNull(Integer id);
}
