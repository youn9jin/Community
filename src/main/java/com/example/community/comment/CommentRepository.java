package com.example.community.comment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findAllByPostPostIdAndDeletedAtIsNull(Integer postId);
    Optional<Comment> findByCommentIdAndDeletedAtIsNull(Integer commentId);
}
