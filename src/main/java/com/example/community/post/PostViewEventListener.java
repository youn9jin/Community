package com.example.community.post;

import com.example.community.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class PostViewEventListener {

    private final PostRepository postRepository;

    @Async
    @EventListener
    @Transactional
    public void handle(PostViewedEvent event) {
        postRepository.incrementViewCount(event.postId());
    }
}
