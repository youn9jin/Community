package com.example.community.likes;

import com.example.community.likes.LikesId;
import com.example.community.post.Post;
import com.example.community.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Likes {
    @EmbeddedId
    private LikesId id;

    @MapsId("postId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id") // 실제 DB에 생성될 FK 컬럼명
    private Post post;

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private LocalDateTime createdAt;

    // 좋아요 생성 시 Post와 User를 기반으로 복합키 생성
    @Builder
    public Likes(Post post, User user) {
        this.id = new LikesId(post.getPostId(), user.getUserId());
        this.post = post;
        this.user = user;
        this.createdAt = LocalDateTime.now();
    }
}
