package com.community.communityboard.service;

import com.community.communityboard.dto.user.EmailVerifyRequestDto;
import com.community.communityboard.dto.user.LoginRequestDto;
import com.community.communityboard.dto.user.SendEmailRequestDto;
import com.community.communityboard.dto.user.SignupRequestDto;
import com.community.communityboard.dto.user.TokenResponseDto;
import com.community.communityboard.dto.user.UpdateUserRequestDto;
import com.community.communityboard.dto.user.UserResponseDto;
import java.util.List;

public interface UserService {

  // 이메일 인증 전 단계 - 이메일 중복체크, 인증코드 발송, Redis에 코드 저장
  void sendVerificationEmail(SendEmailRequestDto requestDto);

  // 이메일 인증 코드 검증
  boolean verifyEmailCode(EmailVerifyRequestDto requestDto);

  // 회원 가입
  UserResponseDto registerUser(SignupRequestDto requestDto);

  // 로그인 - AccessToken, RefreshToken 반환
  TokenResponseDto login(LoginRequestDto requestDto);

  // 로그아웃 - Redis에 저장된 RefreshToken 제거
  void logout(Long userId);

  // 내 정보 조회
  UserResponseDto getMyInfo(Long userId);

  // 상대 회원 정보 조회
  UserResponseDto getUser(Long userId);

  // 전체 회원 조회 (관리자)
  List<UserResponseDto> getAllUsers();

  // 회원 정보 수정
  UserResponseDto updateUser(Long userId, UpdateUserRequestDto requestDto);

  // 회원 탈퇴 - soft delete(status 변경, deletedAt 기록)
  void deleteUser(Long userId);

  // 액세스 토큰 재발급 (리프레시 토큰 함께 재발급)
  TokenResponseDto reissueTokens(String accessToken, String refreshToken);
}
