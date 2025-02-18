package com.community.communityboard.service;

import com.community.communityboard.dto.comment.CommentRequestDto;
import com.community.communityboard.dto.comment.CommentResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommentService {

  // 댓글 등록
  CommentResponseDto createComment(Long userId, Long postId, CommentRequestDto requestDto);

  // 댓글 수정
  CommentResponseDto updateComment(Long userId, Long commentId, CommentRequestDto requestDto);

  // 댓글 삭제 (soft delete)
  void deleteComment(Long userId, Long commentId);

  // 특정 게시글의 댓글 목록 조회 (대댓글 포함, 최신순)
  Page<CommentResponseDto> getComments(Long postId, Pageable pageable);
}
