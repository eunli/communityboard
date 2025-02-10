package com.community.communityboard.controller;

import com.community.communityboard.dto.user.EmailVerifyRequestDto;
import com.community.communityboard.dto.user.LoginRequestDto;
import com.community.communityboard.dto.user.SendEmailRequestDto;
import com.community.communityboard.dto.user.SignupRequestDto;
import com.community.communityboard.dto.user.TokenResponseDto;
import com.community.communityboard.dto.user.UpdateUserRequestDto;
import com.community.communityboard.dto.user.UserResponseDto;
import com.community.communityboard.security.CustomUserDetails;
import com.community.communityboard.service.UserService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  // 이메일 인증코드 발송
  @PostMapping("/email")
  public ResponseEntity<Void> sendVerificationEmail(
      @RequestBody @Valid SendEmailRequestDto dto
  ) {
    userService.sendVerificationEmail(dto);
    return ResponseEntity.ok().build();
  }

  // 이메일 인증 코드 검증
  @PostMapping("/email/verification")
  public ResponseEntity<Boolean> verifyEmailCode(
      @RequestBody @Valid EmailVerifyRequestDto dto
  ) {
    boolean result = userService.verifyEmailCode(dto);
    return ResponseEntity.ok(result);
  }

  // 회원 가입
  @PostMapping("/register")
  public ResponseEntity<UserResponseDto> registerUser(
      @RequestBody @Valid SignupRequestDto dto
  ) {
    UserResponseDto response = userService.registerUser(dto);
    return ResponseEntity.ok(response);
  }

  // 로그인
  @PostMapping("/login")
  public ResponseEntity<TokenResponseDto> login(
      @RequestBody @Valid LoginRequestDto dto
  ) {
    TokenResponseDto tokenResponse = userService.login(dto);
    return ResponseEntity.ok(tokenResponse);
  }

  // 로그아웃
  @PostMapping("/logout/{userId}")
  public ResponseEntity<Void> logout(
      @PathVariable Long userId
  ) {
    userService.logout(userId);
    return ResponseEntity.ok().build();
  }

  // 내 정보 조회
  @GetMapping("/me")
  public ResponseEntity<UserResponseDto> getMyInfo(
      @AuthenticationPrincipal CustomUserDetails userDetails
  ) {
    Long userId = userDetails.getUser().getId();
    UserResponseDto result = userService.getMyInfo(userId);
    return ResponseEntity.ok(result);
  }

  // 상대 회원 정보 조회
  @GetMapping("/{userId}")
  public ResponseEntity<UserResponseDto> getUser(
      @PathVariable Long userId
  ) {
    UserResponseDto result = userService.getUser(userId);
    return ResponseEntity.ok(result);
  }

  // 전체 회원 조회 (관리자)
  @GetMapping("/all")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<List<UserResponseDto>> getAllUsers() {
    List<UserResponseDto> result = userService.getAllUsers();
    return ResponseEntity.ok(result);
  }


  // 회원 정보 수정
  @PatchMapping
  public ResponseEntity<UserResponseDto> updateUser(
      @AuthenticationPrincipal CustomUserDetails userDetails,
      @RequestBody @Valid UpdateUserRequestDto dto
  ) {
    Long userId = userDetails.getUser().getId();
    UserResponseDto result = userService.updateUser(userId, dto);
    return ResponseEntity.ok(result);
  }

  // 회원 탈퇴
  @DeleteMapping
  public ResponseEntity<Void> deleteUser(
      @AuthenticationPrincipal CustomUserDetails userDetails
  ) {
    Long userId = userDetails.getUser().getId();
    userService.deleteUser(userId);
    return ResponseEntity.ok().build();
  }

  // 액세스 토큰 재발급
  @PostMapping("/refresh-token")
  public ResponseEntity<TokenResponseDto> reissueTokens(
      @RequestHeader("Authorization") String accessToken,
      @RequestHeader("Refresh-Token") String refreshToken
  ) {
    if (accessToken.startsWith("Bearer ")) {
      accessToken = accessToken.substring(7);
    }
    TokenResponseDto response = userService.reissueTokens(accessToken, refreshToken);
    return ResponseEntity.ok(response);

  }
}
