package com.community.communityboard.repository;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRepositoryImpl implements RefreshTokenRepository {

  private static final String REFRESH_TOKEN_PREFIX = "RT:";
  private final StringRedisTemplate redisTemplate;

  @Override
  public void save(Long userId, String hashedRefreshToken, Duration duration) {
    ValueOperations<String, String> ops = redisTemplate.opsForValue();
    ops.set(REFRESH_TOKEN_PREFIX + userId, hashedRefreshToken, duration);
  }

  @Override
  public String get(Long userId) {
    ValueOperations<String, String> ops = redisTemplate.opsForValue();
    return ops.get(REFRESH_TOKEN_PREFIX + userId);
  }

  @Override
  public void delete(Long userId) {
    redisTemplate.delete(REFRESH_TOKEN_PREFIX + userId);
  }
}
