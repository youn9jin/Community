package com.example.community.likes;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LikesRepository extends JpaRepository<Likes, LikesId> {
    long countByIdPostId(Integer postId); //게시글 좋아요 수 확인 메서드

    boolean existsByIdPostIdAndIdUserId(Integer postId, Integer userId);
}
