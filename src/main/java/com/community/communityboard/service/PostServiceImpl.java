package com.community.communityboard.service;

import com.community.communityboard.domain.Post;
import com.community.communityboard.domain.User;
import com.community.communityboard.domain.enums.RoleType;
import com.community.communityboard.dto.post.PostRequestDto;
import com.community.communityboard.dto.post.PostResponseDto;
import com.community.communityboard.dto.post.PostSearchCondition;
import com.community.communityboard.exception.CustomException;
import com.community.communityboard.exception.ErrorCode;
import com.community.communityboard.repository.PostRepository;
import com.community.communityboard.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PostServiceImpl  implements PostService {

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

  // 게시글 작성
  @Override
  public PostResponseDto createPost(Long userId, PostRequestDto requestDto) {
    User user = findUserById(userId);

    Post post = Post.builder()
        .user(user)
        .title(requestDto.getTitle())
        .content(requestDto.getContent())
        .imageUrl(requestDto.getImageUrl())
        .build();

    postRepository.save(post);

    return PostResponseDto.builder()
        .id(post.getId())
        .title(post.getTitle())
        .content(post.getContent())
        .imageUrl(post.getImageUrl())
        .writerNickname(post.getUser().getNickname())
        .commentCount(post.getCommentCount())
        .createdAt(post.getCreatedAt())
        .updatedAt(post.getUpdatedAt())
        .build();
  }

  // 게시글 상세 조회
  @Override
  public PostResponseDto getPost(Long postId) {
    Post post = findPostById(postId);
    return PostResponseDto.builder()
        .id(post.getId())
        .title(post.getTitle())
        .content(post.getContent())
        .imageUrl(post.getImageUrl())
        .writerNickname(post.getUser().getNickname())
        .commentCount(post.getCommentCount())
        .createdAt(post.getCreatedAt())
        .updatedAt(post.getUpdatedAt())
        .build();
  }

  // 게시글 전체 조회
  @Override
  public Page<PostResponseDto> getAllPosts(Pageable pageable) {
    Page<Post> postPage = postRepository.findAll(pageable);

    return postPage.map(post -> PostResponseDto.builder()
        .id(post.getId())
        .title(post.getTitle())
        .content(post.getContent())
        .imageUrl(post.getImageUrl())
        .writerNickname(post.getUser().getNickname())
        .commentCount(post.getCommentCount())
        .createdAt(post.getCreatedAt())
        .updatedAt(post.getUpdatedAt())
        .build());
  }

  // 게시글 검색
  @Override
  public Page<PostResponseDto> searchPosts(PostSearchCondition condition, Pageable pageable) {
    String keyword = condition.getKeyword() == null ? "" : condition.getKeyword().trim();
    Page<Post> postPage = postRepository.searchPostsByKeyword(keyword, pageable);
    return postPage.map(post -> PostResponseDto.builder()
        .id(post.getId())
        .title(post.getTitle())
        .content(post.getContent())
        .imageUrl(post.getImageUrl())
        .writerNickname(post.getUser().getNickname())
        .commentCount(post.getCommentCount())
        .createdAt(post.getCreatedAt())
        .updatedAt(post.getUpdatedAt())
        .build());
  }

  // 게시글 수정
  @Override
  public PostResponseDto updatePost(Long userId, Long postId, PostRequestDto requestDto) {
    User user = findUserById(userId);
    Post post = findPostById(postId);

    if (!post.getUser().getId().equals(userId) &&
        !user.getRole().getName().equals(RoleType.ADMIN)) {
      throw new CustomException(ErrorCode.UNAUTHORIZED_POST_ACCESS);
    }

    post.setTitle(requestDto.getTitle());
    post.setContent(requestDto.getContent());
    post.setImageUrl(requestDto.getImageUrl());

    return PostResponseDto.builder()
        .id(post.getId())
        .title(post.getTitle())
        .content(post.getContent())
        .imageUrl(post.getImageUrl())
        .writerNickname(post.getUser().getNickname())
        .commentCount(post.getCommentCount())
        .createdAt(post.getCreatedAt())
        .updatedAt(post.getUpdatedAt())
        .build();
  }

  // 게시글 삭제
  @Override
  public void deletePost(Long userId, Long postId) {
    User user = findUserById(userId);
    Post post = findPostById(postId);

    if (!post.getUser().getId().equals(userId) &&
        !user.getRole().getName().equals(RoleType.ADMIN)) {
      throw new CustomException(ErrorCode.UNAUTHORIZED_POST_ACCESS);
    }

    postRepository.delete(post);
  }
}
