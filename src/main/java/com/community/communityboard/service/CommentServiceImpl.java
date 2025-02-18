package com.community.communityboard.service;

import com.community.communityboard.domain.Comment;
import com.community.communityboard.domain.Post;
import com.community.communityboard.domain.User;
import com.community.communityboard.domain.enums.RoleType;
import com.community.communityboard.dto.comment.CommentRequestDto;
import com.community.communityboard.dto.comment.CommentResponseDto;
import com.community.communityboard.exception.CustomException;
import com.community.communityboard.exception.ErrorCode;
import com.community.communityboard.repository.CommentRepository;
import com.community.communityboard.repository.PostRepository;
import com.community.communityboard.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentServiceImpl implements CommentService {

  private final CommentRepository commentRepository;
  private final PostRepository postRepository;
  private final UserRepository userRepository;

  private Post findPostById(Long id) {
    return postRepository.findById(id)
        .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));
  }

  private User findUserById(Long id) {
    return userRepository.findById(id)
        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
  }

  private Comment findCommentById(Long id) {
    return commentRepository.findById(id)
        .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));
  }

  // 댓글 등록
  @Override
  public CommentResponseDto createComment(Long userId, Long postId, CommentRequestDto requestDto) {
    User user = findUserById(userId);
    Post post = findPostById(postId);

    Comment parent = null;
    if (requestDto.getParentId() != null) {
      parent = findCommentById(requestDto.getParentId());
    }

    Comment comment = Comment.builder()
        .user(user)
        .post(post)
        .parent(parent)
        .content(requestDto.getContent())
        .build();

    commentRepository.save(comment);

    post.setCommentCount(post.getCommentCount() + 1);

    return CommentResponseDto.builder()
        .id(comment.getId())
        .writerNickname(comment.getUser().getNickname())
        .postId(comment.getPost().getId())
        .parentId(comment.getParent() == null ? null : comment.getParent().getId())
        .content(comment.getContent())
        .createdAt(comment.getCreatedAt())
        .updatedAt(comment.getUpdatedAt())
        .deletedAt(comment.getDeletedAt())
        .build();
  }

  // 댓글 수정
  @Override
  public CommentResponseDto updateComment(Long userId, Long commentId, CommentRequestDto requestDto) {
    User user = findUserById(userId);
    Comment comment = findCommentById(commentId);

    if (!comment.getUser().getId().equals(userId) &&
        !user.getRole().getName().equals(RoleType.ADMIN)) {
      throw new CustomException(ErrorCode.UNAUTHORIZED_COMMENT_ACCESS);
    }

    comment.setContent(requestDto.getContent());

    return CommentResponseDto.builder()
        .id(comment.getId())
        .writerNickname(comment.getUser().getNickname())
        .postId(comment.getPost().getId())
        .parentId(comment.getParent() == null ? null : comment.getParent().getId())
        .content(comment.getContent())
        .createdAt(comment.getCreatedAt())
        .updatedAt(comment.getUpdatedAt())
        .deletedAt(comment.getDeletedAt())
        .build();
  }

  // 댓글 삭제 (soft delete)
  @Override
  public void deleteComment(Long userId, Long commentId) {
    User user = findUserById(userId);
    Comment comment = findCommentById(commentId);
    Post post = comment.getPost();

    if (!comment.getUser().getId().equals(userId) &&
        !user.getRole().getName().equals(RoleType.ADMIN)) {
      throw new CustomException(ErrorCode.UNAUTHORIZED_COMMENT_ACCESS);
    }

    comment.setDeletedAt(LocalDateTime.now());
    post.setCommentCount(Math.max(0, post.getCommentCount() - 1));
  }

  // 특정 게시글의 댓글 목록 조회 (대댓글 포함, 최신순)
  @Override
  public Page<CommentResponseDto> getComments(Long postId, Pageable pageable) {
    Post post = findPostById(postId);
    Page<Comment> commentPage = commentRepository.findAllCommentsByPost(post, pageable);

    return commentPage.map(comment -> CommentResponseDto.builder()
        .id(comment.getId())
        .writerNickname(comment.getUser().getNickname())
        .postId(comment.getPost().getId())
        .parentId(comment.getParent() == null ? null : comment.getParent().getId())
        .content(comment.getContent())
        .createdAt(comment.getCreatedAt())
        .updatedAt(comment.getUpdatedAt())
        .deletedAt(comment.getDeletedAt())
        .build());
  }
}
