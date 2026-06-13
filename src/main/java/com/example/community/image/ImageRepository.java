package com.example.community.image;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ImageRepository extends JpaRepository<Image, Integer> {

    // 게시글의 활성 이미지 조회
    List<Image> findByPostPostIdAndActiveTrue(Integer postId);

    // 유저의 활성 프로필 이미지 조회 (user.userId 경로로 탐색)
    Optional<Image> findByUserUserIdAndActiveTrue(Integer userId);

    // 고아 이미지 정리용: 비활성 + 기준 시간 이전 생성
    List<Image> findByActiveFalseAndCreatedAtBefore(LocalDateTime threshold);
}