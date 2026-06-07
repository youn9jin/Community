package com.example.community.post;

import com.example.community.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer postId;

    @Column(length = 26)
    @Setter private String title;

    @Setter private String content;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    @Setter private LocalDateTime deletedAt;

    @Setter private Integer viewCount;
    @Setter private Integer likeCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public Post(String title, String content, User user) {
        this.title = title;
        this.content = content;
        this.user = user;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.viewCount = 0;
        this.likeCount = 0;
    }
}
