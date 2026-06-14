package com.example.community.user;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface UserRepository extends JpaRepository <User, Integer> {
    boolean existsByEmail(String email);
    boolean existsByNickname(String nickname);
    boolean existsByNicknameAndUserIdNot(String nickname, Integer userId);
    boolean existsByUserIdAndStatus(Integer userId, UserStatus status);
    Optional<User> findByEmail(String email);
}
