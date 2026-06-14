package com.example.community.likes;

import com.example.community.global.exception.LikesNotFoundException;
import com.example.community.global.exception.PostNotFoundException;
import com.example.community.global.exception.BadRequestException;
import com.example.community.likes.dto.LikesResponseDTO;
import com.example.community.post.Post;
import com.example.community.post.repository.PostRepository;
import com.example.community.user.User;
import com.example.community.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LikesService {

    private final LikesRepository likesRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    public LikesResponseDTO addLike(Integer postId, Integer userId){

        //1. 좋아요 누르려는 게시글 존재여부 검사
        Post post = postRepository.findByPostIdAndDeletedAtIsNull(postId)
                .orElseThrow(()-> new PostNotFoundException(postId));

        // 2. 중복 좋아요 검사
        LikesId likesId = new LikesId(postId, userId);
        if (likesRepository.existsById(likesId)) {
            throw new BadRequestException("already liked post");
        }

        //3. UserId 조회
        User user = userRepository.getReferenceById(userId);

        Likes likes = Likes.builder()
                .post(post)
                .user(user)
                .build();

        likesRepository.save(likes);
        post.incrementLikeCount();

        return new LikesResponseDTO(post.getPostId(), post.getLikeCount());
    }

    @Transactional
    public void removeLike(Integer postId, Integer userId){
        Post post = postRepository.findByPostIdAndDeletedAtIsNull(postId)
                .orElseThrow(()-> new PostNotFoundException(postId));

        //좋아요가 없는 경우에 delete하는 경우 handling
        LikesId likesId = new LikesId(postId, userId);
        if (!likesRepository.existsById(likesId)) {
            throw new LikesNotFoundException(postId);
        }

        likesRepository.deleteById(likesId);
        post.decrementLikeCount();
    }
}
