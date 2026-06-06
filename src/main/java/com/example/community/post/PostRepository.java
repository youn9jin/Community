package com.example.community.post;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Integer> {
    Page<Post> findAllByDeletedAtIsNull(Pageable pageable);
    Optional<Post> findByIdAndDeletedAtIsNull(Integer id);
}
