package com.example.community.post;

import com.example.community.comment.CommentRepository;
import com.example.community.comment.Comment;
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

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        // 게시글과 작성자를 먼저 조회
        Page<Post> posts = repository.findActivePosts(pageable);

        // 현재 페이지에 등장한 작성자 id만 모아 프로필 이미지를 한 번에 조회 후 map으로 변환
        List<Integer> userIds = posts.getContent().stream()
                .map(post -> post.getUser().getUserId())
                .distinct() // 중복 제거
                .toList();

        // userId -> thumbnailPath 형태로 만들어 WriterDTO 생성 시 재사용
        Map<Integer, String> profileImageMap = getProfileImageMap(userIds);

        return posts
                .map(post -> new PostListResposneDTO(
                        post.getPostId(),
                        post.getTitle(),
                        post.getCreatedAt(),
                        new WriterDTO(
                                post.getUser().getUserId(),
                                post.getUser().getNickname(),
                                // 미리 만든 Map에서 작성자 프로필 썸네일 조회
                                profileImageMap.get(post.getUser().getUserId())
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

        String imageUrl = imageRepository.findByPostPostIdAndActiveTrue(post.getPostId())
                .map(Image::getStoragePath)
                .orElse(null);
        String writerProfileImgUrl = imageRepository.findByUserUserIdAndActiveTrue(post.getUser().getUserId())
                .map(Image::getThumbnailPath)
                .orElse(null);

        // 댓글과 댓글 작성자를 함께 조회
        List<Comment> comments = commentRepository.findActiveCommentsByPostId(post.getPostId());

        // 댓글 작성자 id를 모아 댓글 작성자 프로필 이미지를 한 번에 조회하기 위한 준비
        List<Integer> commentUserIds = comments.stream()
                .map(comment -> comment.getUser().getUserId())
                .distinct()
                .toList();

        // 댓글 작성자 userId -> thumbnailPath
        Map<Integer, String> commentProfileImageMap = getProfileImageMap(commentUserIds);

        return new PostDetailResponseDTO(
                post.getPostId(),
                post.getTitle(),
                post.getCreatedAt(),
                new WriterDTO(
                        post.getUser().getUserId(),
                        post.getUser().getNickname(),
                        writerProfileImgUrl
                ),
                post.getUpdatedAt(),
                post.getViewCount(),
                (int) likesRepository.countByIdPostId(post.getPostId()),
                post.getContent(),
                imageUrl,
                getCommentResponses(comments, commentProfileImageMap)
        );
    }

    private List<CommentResponseDTO> getCommentResponses(List<Comment> comments, Map<Integer, String> profileImageMap) {
        return comments
                .stream() //댓글 목록 하나씩 mapping 할 수 있도록 stream 활용
                .map(comment -> new CommentResponseDTO(
                        comment.getCommentId(),
                        new WriterDTO(
                                comment.getUser().getUserId(),
                                comment.getUser().getNickname(),
                                // 미리 만든 Map에서 댓글 작성자 프로필 썸네일 조회
                                profileImageMap.get(comment.getUser().getUserId())
                        ),
                        comment.getCreatedAt(),
                        comment.getContent()
                ))
                .toList(); //mapping된 결과 list로 모으기
    }

    private Map<Integer, String> getProfileImageMap(List<Integer> userIds) {
        // 빈 IN 쿼리 방지
        if (userIds.isEmpty()) {
            return Collections.emptyMap();
        }

        // 여러 userId의 활성 프로필 이미지를 한 번에 조회한 뒤, userId로 바로 찾을 수 있게 Map으로 변환
        return imageRepository.findByUserUserIdInAndActiveTrue(userIds)
                .stream()
                .filter(image -> image.getThumbnailPath() != null)
                .collect(Collectors.toMap(
                        image -> image.getUser().getUserId(),
                        Image::getThumbnailPath,
                        // 같은 userId에 이미지가 여러 개 나오면 먼저 조회된 값을 유지
                        (first, second) -> first
                ));
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

        if (request.getImageId() != null) {
            imageRepository.findByPostPostIdAndActiveTrue(postId)
                    .ifPresent(img -> img.deactivate());
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

        imageRepository.findByPostPostIdAndActiveTrue(postId)
                .ifPresent(img -> img.deactivate());

        post.softDelete();
    }
}
