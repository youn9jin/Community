package com.example.community.comment;

import com.example.community.comment.dto.CommentRequestDTO;
import com.example.community.comment.dto.CommentResponseDTO;
import com.example.community.comment.dto.CommentUpdateResponseDTO;
import com.example.community.global.exception.CommentNotFoundException;
import com.example.community.global.exception.ForbiddenException;
import com.example.community.global.exception.PostNotFoundException;
import com.example.community.global.exception.UnauthorizedException;
import com.example.community.post.Post;
import com.example.community.post.repository.PostRepository;
import com.example.community.user.User;
import com.example.community.user.UserRepository;
import com.example.community.user.UserStatus;
import com.example.community.user.dto.WriterDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository repository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    //1. 댓글 작성
    @Transactional
    public CommentResponseDTO createComment(Integer postId, Integer userId, CommentRequestDTO request){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UnauthorizedException());

        if (user.getStatus() == UserStatus.DELETED) {
            throw new UnauthorizedException();
        }

        Post post = postRepository.findByPostIdAndDeletedAtIsNull(postId)
                .orElseThrow(() -> new PostNotFoundException(postId));

        Comment comment = new Comment(request.getContent(), user, post);

        Comment savedComment = repository.save(comment);

        return new CommentResponseDTO(
                savedComment.getCommentId(),
                new WriterDTO(
                        savedComment.getUser().getUserId(),
                        savedComment.getUser().getNickname(),
                        null
                ),
                savedComment.getCreatedAt(),
                savedComment.getContent()
        );
    }

    //2. 댓글 수정
    @Transactional
    public CommentUpdateResponseDTO updateComment(Integer postId, Integer commentId, Integer userId, CommentRequestDTO request){
        Comment comment = repository.findActiveCommentById(commentId)
                .orElseThrow(() -> new CommentNotFoundException(commentId));

        if (!comment.getPost().getPostId().equals(postId)) { // postId와 댓글의 실제 게시글이 같은지 검사
            throw new CommentNotFoundException(commentId);
        }

        if (comment.getUser().getStatus() == UserStatus.DELETED) {
            throw new UnauthorizedException();
        }

        if (!comment.getUser().getUserId().equals(userId)) {
            throw new ForbiddenException();
        }

        comment.update(request.getContent());

        return new CommentUpdateResponseDTO(
                comment.getContent()
                );
    }

    //3. 댓글 삭제
    @Transactional
    public void deleteComment(Integer postId, Integer commentId, Integer userId){
        Comment comment = repository.findActiveCommentById(commentId)
                .orElseThrow(() -> new CommentNotFoundException(commentId));

        if (!comment.getPost().getPostId().equals(postId)) { // postId와 댓글의 실제 게시글이 같은지 검사
            throw new CommentNotFoundException(commentId);
        }

        if (comment.getUser().getStatus() == UserStatus.DELETED) {
            throw new UnauthorizedException();
        }

        if (!comment.getUser().getUserId().equals(userId)) {
            throw new ForbiddenException();
        }

        comment.softDelete();
    }
}
