package com.example.community.comment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findAllByPostIdAndDeletedAtIsNull(Integer postId);
    Optional<Comment> findByIdAndDeletedAtIsNull(Integer commentId);
}
