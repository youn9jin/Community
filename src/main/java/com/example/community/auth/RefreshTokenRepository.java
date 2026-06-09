package com.example.community.auth;

import com.example.community.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Integer> {
    Optional<RefreshToken> findByRefreshToken(String refreshToken);

    // 특정 user의 RT 전체 삭제
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("DELETE FROM RefreshToken r WHERE r.user = :user")
    void deleteByUser(@Param("user") User user);

    // 특정 RT 삭제 -> RTR 용도
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("DELETE FROM RefreshToken r WHERE r = :refreshToken")
    void deleteByRefreshToken(@Param("refreshToken") RefreshToken refreshToken);
}