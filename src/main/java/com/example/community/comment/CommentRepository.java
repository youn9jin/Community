package com.example.community.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    // 댓글 작성자 정보와 user를 함께 조회 -> N+1문제 방지
    @Query("SELECT c FROM Comment c JOIN FETCH c.user WHERE c.post.postId = :postId AND c.deletedAt IS NULL")
    List<Comment> findActiveCommentsByPostId(@Param("postId") Integer postId);

    @Query("SELECT c FROM Comment c WHERE c.commentId = :commentId AND c.deletedAt IS NULL")
    Optional<Comment> findActiveCommentById(@Param("commentId") Integer commentId);
}
