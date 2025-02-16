package com.community.communityboard.service;

import com.community.communityboard.dto.post.PostRequestDto;
import com.community.communityboard.dto.post.PostResponseDto;
import com.community.communityboard.dto.post.PostSearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostService {

  // 게시글 작성
  PostResponseDto createPost(Long userId, PostRequestDto requestDto);

  // 게시글 상세 조회
  PostResponseDto getPost(Long postId);

  // 게시글 전체 조회
  Page<PostResponseDto> getAllPosts(Pageable pageable);

  // 게시글 검색
  Page<PostResponseDto> searchPosts(PostSearchCondition condition, Pageable pageable);

  // 게시글 수정
  PostResponseDto updatePost(Long userId, Long postId, PostRequestDto requestDto);

  // 게시글 삭제
  void deletePost(Long userId, Long postId);

}
