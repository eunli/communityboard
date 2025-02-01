package com.community.communityboard.dto.user;

import com.community.communityboard.domain.enums.RoleType;
import com.community.communityboard.domain.enums.UserStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserResponseDto {
  private Long id;
  private String email;
  private String nickname;
  private UserStatus status;
  private RoleType role;
}
