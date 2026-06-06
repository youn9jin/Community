package com.example.community.image;

import com.example.community.post.Post;
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
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer imageId;

    @Column(name = "is_active")
    @Setter private boolean active;

    private LocalDateTime createdAt;

    @Setter
    @Column(unique = true, length = 500)
    private String storagePath;

    @Column(unique = true, length = 500)
    @Setter private String thumbnailPath;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", referencedColumnName = "post_id")
    private Post post;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User user;

    public Image(boolean active, String storagePath, String thumbnailPath, Post post, User user) {
        this.active = active;
        this.createdAt = LocalDateTime.now();
        this.storagePath = storagePath;
        this.thumbnailPath = thumbnailPath;
        this.post = post;
        this.user = user;
    }
}
