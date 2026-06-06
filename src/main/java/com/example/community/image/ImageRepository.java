package com.example.community.image;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ImageRepository extends JpaRepository<Image, Integer> {
    List<Image> findByPostId(Integer postId);   // 게시글의 이미지 목록 조회
    Optional<Image> findByUserId(Integer userId); // 유저 프로필 이미지 조회
}
