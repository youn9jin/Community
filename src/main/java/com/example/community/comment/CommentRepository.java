package com.example.community.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    @Query("SELECT c FROM Comment c WHERE c.post.postId = :postId AND c.deletedAt IS NULL")
    List<Comment> findActiveCommentsByPostId(@Param("postId") Integer postId);

    @Query("SELECT c FROM Comment c WHERE c.commentId = :commentId AND c.deletedAt IS NULL")
    Optional<Comment> findActiveCommentById(@Param("commentId") Integer commentId);
}
