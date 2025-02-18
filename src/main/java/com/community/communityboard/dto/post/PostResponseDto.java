package com.community.communityboard.dto.post;

import com.community.communityboard.dto.comment.CommentResponseDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

@Getter
@Setter
@Builder
@JsonInclude(Include.NON_NULL)
public class PostResponseDto {
  private Long id;
  private String title;
  private String content;
  private String imageUrl;
  private String writerNickname;
  private int commentCount;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private Page<CommentResponseDto> comments;
}
