package com.community.communityboard.controller;

import com.community.communityboard.dto.post.PostRequestDto;
import com.community.communityboard.dto.post.PostResponseDto;
import com.community.communityboard.dto.post.PostSearchCondition;
import com.community.communityboard.security.CustomUserDetails;
import com.community.communityboard.service.PostService;
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
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

  private final PostService postService;

  // 게시글 작성
  @PostMapping
  public ResponseEntity<PostResponseDto> createPost(
      @AuthenticationPrincipal CustomUserDetails userDetails,
      @RequestBody @Valid PostRequestDto dto
  ) {
    Long userId = userDetails.getUser().getId();
    PostResponseDto result = postService.createPost(userId, dto);
    return ResponseEntity.ok(result);
  }

  // 게시글 상세 조회
  @GetMapping("/{postId}")
  public ResponseEntity<PostResponseDto> getPost(
      @PathVariable Long postId
  ) {
    PostResponseDto result = postService.getPost(postId);
    return ResponseEntity.ok(result);
  }

  // 게시글 전체 조회
  @GetMapping
  public ResponseEntity<Page<PostResponseDto>> getAllPosts(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(defaultValue = "createdAt") String sortType // sortType이 "commentCount"이면 댓글수 정렬
  ) {
    Sort sort = Sort.by(sortType).descending();
    Pageable pageable = PageRequest.of(page, size, sort);
    Page<PostResponseDto> result = postService.getAllPosts(pageable);
    return ResponseEntity.ok(result);
  }

  // 게시글 검색
  @GetMapping("/search")
  public ResponseEntity<Page<PostResponseDto>> searchPosts(
      @RequestParam(defaultValue = "") String keyword,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size
  ) {
    Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
    PostSearchCondition condition = new PostSearchCondition();
    condition.setKeyword(keyword);

    Page<PostResponseDto> result = postService.searchPosts(condition, pageable);
    return ResponseEntity.ok(result);
  }

  // 게시글 수정
  @PatchMapping("/{postId}")
  public ResponseEntity<PostResponseDto> updatePost(
      @AuthenticationPrincipal CustomUserDetails userDetails,
      @PathVariable Long postId,
      @RequestBody @Valid PostRequestDto dto
  ) {
    Long userId = userDetails.getUser().getId();
    PostResponseDto result = postService.updatePost(userId, postId, dto);
    return ResponseEntity.ok(result);
  }

  // 게시글 삭제
  @DeleteMapping("/{postId}")
  public ResponseEntity<Void> deletePost(
      @AuthenticationPrincipal CustomUserDetails userDetails,
      @PathVariable Long postId
  ) {
    Long userId = userDetails.getUser().getId();
    postService.deletePost(userId, postId);
    return ResponseEntity.ok().build();
  }
}
