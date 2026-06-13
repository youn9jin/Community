package com.example.community.post;

import com.example.community.comment.CommentRepository;
import com.example.community.comment.dto.CommentResponseDTO;
import com.example.community.global.exception.BadRequestException;
import com.example.community.global.exception.ForbiddenException;
import com.example.community.global.exception.ImageNotFoundException;
import com.example.community.global.exception.PostNotFoundException;
import com.example.community.global.exception.UnauthorizedException;
import com.example.community.image.Image;
import com.example.community.image.ImageRepository;
import com.example.community.likes.LikesRepository;
import com.example.community.post.dto.PostDetailResponseDTO;
import com.example.community.post.dto.PostListResposneDTO;
import com.example.community.post.dto.PostRequestDTO;
import com.example.community.post.dto.PostResponseDTO;
import com.example.community.post.repository.PostRepository;
import com.example.community.user.User;
import com.example.community.user.UserRepository;
import com.example.community.user.dto.WriterDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository repository;
    private final LikesRepository likesRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final ImageRepository imageRepository;

    //1. 게시글 목록 조회
    @Transactional(readOnly = true)
    public Page<PostListResposneDTO> getPosts(Pageable pageable){
        return repository.findActivePosts(pageable)
                .map(post -> new PostListResposneDTO(
                        post.getPostId(),
                        post.getTitle(),
                        post.getCreatedAt(),
                        new WriterDTO(
                                post.getUser().getUserId(),
                                post.getUser().getNickname(),
                                null
                        ),
                        post.getUpdatedAt(),
                        post.getViewCount(), // 조회 수
                        (int)(likesRepository.countByIdPostId(post.getPostId())), //좋아요 수
                        commentRepository.findActiveCommentsByPostId(post.getPostId()).size() //댓글 수
                ));
    }

    // 2. 게시글 상세 조회
    @Transactional(readOnly = true)
    public PostDetailResponseDTO getPost(Integer postId){
        Post post = repository.findByPostIdAndDeletedAtIsNull(postId)
                .orElseThrow(() -> new PostNotFoundException(postId));

        List<String> imageUrls = imageRepository.findByPostPostIdAndActiveTrue(post.getPostId())
                .stream()
                .map(image -> image.getStoragePath())
                .toList();

        return new PostDetailResponseDTO(
                post.getPostId(),
                post.getTitle(),
                post.getCreatedAt(),
                new WriterDTO(
                        post.getUser().getUserId(),
                        post.getUser().getNickname(),
                        null
                ),
                post.getUpdatedAt(),
                post.getViewCount(),
                (int) likesRepository.countByIdPostId(post.getPostId()),
                post.getContent(),
                imageUrls,
                getCommentResponses(post.getPostId())
        );
    }

    private List<CommentResponseDTO> getCommentResponses(Integer postId) {
        return commentRepository.findActiveCommentsByPostId(postId)
                .stream() //댓글 목록 하나씩 mapping 할 수 있도록 stream 활용
                .map(comment -> new CommentResponseDTO(
                        comment.getCommentId(),
                        new WriterDTO(
                                comment.getUser().getUserId(),
                                comment.getUser().getNickname(),
                                null
                        ),
                        comment.getCreatedAt(),
                        comment.getContent()
                ))
                .toList(); //mapping된 결과 list로 모으기
    }

    //3. 게시글 작성
    @Transactional
    public PostResponseDTO createPost(Integer userId, PostRequestDTO request){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UnauthorizedException());

        Post post = new Post(
                request.getTitle(),
                request.getContent(),
                user
        );

        Post savedPost = repository.save(post);

        if(request.getImageId() != null){
            Image image = imageRepository.findById(request.getImageId())
                    .orElseThrow(() -> new ImageNotFoundException("Image not found."));
            if (!image.getUploadedBy().getUserId().equals(userId)) {
                throw new ForbiddenException("You are not authorized to use this image.");
            }
            if (image.isActive()) {
                throw new BadRequestException("Image is already in use.");
            }
            image.attachToPost(post);
        }

        return new PostResponseDTO(
                savedPost.getPostId(),
                savedPost.getTitle(),
                new WriterDTO(
                        savedPost.getUser().getUserId(),
                        savedPost.getUser().getNickname(),
                        null
                ),
                savedPost.getContent(),
                savedPost.getCreatedAt(),
                savedPost.getUpdatedAt()
        );
    }

    //4. 게시글 수정
    @Transactional
    public PostResponseDTO updatePost(Integer postId, Integer userId, PostRequestDTO request){
        Post post = repository.findByPostIdAndDeletedAtIsNull(postId)
                .orElseThrow(() -> new PostNotFoundException(postId));

        if (!post.getUser().getUserId().equals(userId)) { //게시글 작성자가 아닌 경우 403 에러 처리
            throw new ForbiddenException();
        }

        post.update(request.getTitle(), request.getContent());

        return new PostResponseDTO(
                post.getPostId(),
                post.getTitle(),
                new WriterDTO(
                        post.getUser().getUserId(),
                        post.getUser().getNickname(),
                        null
                ),
                post.getContent(),
                post.getCreatedAt(),
                post.getUpdatedAt()
        );
    }

    //5. 게시글 삭제
    @Transactional
    public void deletePost(Integer postId, Integer userId){
        Post post = repository.findByPostIdAndDeletedAtIsNull(postId)
                .orElseThrow(() -> new PostNotFoundException(postId));

        if (!post.getUser().getUserId().equals(userId)) { //게시글 작성자가 아닌 경우 403 에러 처리
            throw new ForbiddenException("don't have rights to delete");
        }

        post.softDelete();
    }
}
