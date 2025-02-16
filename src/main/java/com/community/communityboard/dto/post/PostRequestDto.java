package com.community.communityboard.dto.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostRequestDto {

  @NotBlank
  @Size(min = 1, max = 50)
  private String title;

  @NotBlank
  @Size(max = 10000)
  private String content;

  private String imageUrl;

}
