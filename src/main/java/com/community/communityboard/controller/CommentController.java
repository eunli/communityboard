package com.community.communityboard.controller;

import com.community.communityboard.dto.comment.CommentRequestDto;
import com.community.communityboard.dto.comment.CommentResponseDto;
import com.community.communityboard.security.CustomUserDetails;
import com.community.communityboard.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

  private final CommentService commentService;

  // 댓글 등록
  @PostMapping("/{postId}")
  public ResponseEntity<CommentResponseDto> createComment(
      @AuthenticationPrincipal CustomUserDetails userDetails,
      @PathVariable Long postId,
      @RequestBody @Valid CommentRequestDto dto
  ) {
    Long userId = userDetails.getUser().getId();
    CommentResponseDto result = commentService.createComment(userId, postId, dto);
    return ResponseEntity.ok(result);
  }

  // 댓글 수정
  @PatchMapping("/{commentId}")
  public ResponseEntity<CommentResponseDto> updateComment(
      @AuthenticationPrincipal CustomUserDetails userDetails,
      @PathVariable Long commentId,
      @RequestBody @Valid CommentRequestDto dto
  ) {
    Long userId = userDetails.getUser().getId();
    CommentResponseDto result = commentService.updateComment(userId, commentId, dto);
    return ResponseEntity.ok(result);
  }

  // 댓글 삭제 (soft delete)
  @DeleteMapping("/{commentId}")
  public ResponseEntity<Void> deleteComment(
      @AuthenticationPrincipal CustomUserDetails userDetails,
      @PathVariable Long commentId
  ) {
    Long userId = userDetails.getUser().getId();
    commentService.deleteComment(userId, commentId);
    return ResponseEntity.ok().build();
  }

  // 특정 게시글의 댓글 목록 조회 (대댓글 포함, 최신순)
  @GetMapping("/post/{postId}")
  public ResponseEntity<Page<CommentResponseDto>> getComments(
      @PathVariable Long postId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size
  ) {
    Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
    Page<CommentResponseDto> result = commentService.getComments(postId, pageable);
    return ResponseEntity.ok(result);
  }
}
