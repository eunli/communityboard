package com.community.communityboard.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupRequestDto {

  @NotBlank
  @Email
  @Size(min = 3, max = 50)
  private String email;

  @NotBlank
  @Size(min = 8, max = 20, message = "비밀번호는 8~20자리여야 합니다.")
  @Pattern(
      regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&*]).{8,20}$",
      message = "비밀번호는 영문 대소문자, 숫자, 특수문자를 포함해야 합니다."
  )
  private String password;

  @NotBlank
  @Size(min = 3, max = 10)
  private String nickname;
}
