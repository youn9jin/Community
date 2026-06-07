package com.example.community.image;

import com.example.community.post.Post;
import com.example.community.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer imageId;

    @Column(name = "is_active")
    private boolean active;

    private LocalDateTime createdAt;

    @Column(unique = true, length = 500)
    private String storagePath;

    @Column(unique = true, length = 500)
    private String thumbnailPath;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // 이미지 저장 시 필요한 값으로 Image 엔티티 생성
    @Builder
    public Image(boolean active, String storagePath, String thumbnailPath, Post post, User user) {
        this.active = active;
        this.createdAt = LocalDateTime.now();
        this.storagePath = storagePath;
        this.thumbnailPath = thumbnailPath;
        this.post = post;
        this.user = user;
    }
}
