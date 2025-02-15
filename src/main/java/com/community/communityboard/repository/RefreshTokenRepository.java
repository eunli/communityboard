package com.community.communityboard.repository;

import java.time.Duration;

public interface RefreshTokenRepository {

  // Refresh Token 저장
  void save(Long userId, String hashedRefreshToken, Duration duration);

  // Refresh Token 조회
  String get(Long userId);

  // Refresh Token 삭제
  void delete(Long userId);
}
