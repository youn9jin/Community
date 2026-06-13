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

    @Column(length = 500)
    private String thumbnailPath;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by", nullable = false)
    private User uploadedBy;

    // 업로드 직후 고아 상태로 생성
    public static Image createOrphan(String storagePath, String thumbnailPath, User uploadedBy) {
        Image image = new Image();
        image.active = false;
        image.createdAt = LocalDateTime.now();
        image.storagePath = storagePath;
        image.thumbnailPath = thumbnailPath;
        image.uploadedBy = uploadedBy;
        return image;
    }


    // 게시글에 연결
    public void attachToPost(Post post) {
        this.post = post;
        this.active = true;
    }

    // 프로필에 연결
    public void attachToUser(User user) {
        this.user = user;
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }
}
