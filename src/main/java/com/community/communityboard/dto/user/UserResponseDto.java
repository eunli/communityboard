package com.community.communityboard.dto.user;

import com.community.communityboard.domain.enums.RoleType;
import com.community.communityboard.domain.enums.UserStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@JsonInclude(Include.NON_NULL)
public class UserResponseDto {
  private Long id;
  private String email;
  private String nickname;
  private UserStatus status;
  private RoleType role;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private LocalDateTime deletedAt;
}
