package com.community.communityboard.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentRequestDto {

  private Long parentId;

  @NotBlank
  @Size(min = 1, max = 1000)
  private String content;
}
