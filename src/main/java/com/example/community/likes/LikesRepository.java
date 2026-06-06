package com.example.community.likes;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikesRepository extends JpaRepository<Likes, LikesId> {
    Optional<Likes> findByPostIdAndUserId(Integer postId, Integer userId);
    boolean existsByPostIdAndUserId(Integer postId, Integer userId);
    long countByPostId(Integer postId);
}
