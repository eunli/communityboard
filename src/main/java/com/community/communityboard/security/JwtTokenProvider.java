package com.community.communityboard.security;

import com.community.communityboard.domain.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtTokenProvider {

  private final Key key;
  private final long accessTokenValidity;
  private final long refreshTokenValidity;

  public JwtTokenProvider(
      @Value("${jwt.secret-key}") String secretKey,
      @Value("${jwt.access-token}") long accessTokenValiditySeconds,
      @Value("${jwt.refresh-token}") long refreshTokenValiditySeconds
  ) {
    this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
    this.accessTokenValidity = accessTokenValiditySeconds * 1000;
    this.refreshTokenValidity = refreshTokenValiditySeconds * 1000;
  }

  // Access token 생성
  public String createAccessToken(User user) {
    Date now = new Date();
    Date validity = new Date(now.getTime() + accessTokenValidity);

    return Jwts.builder()
        .setSubject(String.valueOf(user.getId()))
        .claim("role", user.getRole().getName().name())
        .setIssuedAt(now)
        .setExpiration(validity)
        .signWith(key, SignatureAlgorithm.HS256)
        .compact();
  }

  // Refresh token 생성
  public String createRefreshToken(User user) {
    Date now = new Date();
    Date validity = new Date(now.getTime() + refreshTokenValidity);

    return Jwts.builder()
        .setSubject(String.valueOf(user.getId()))
        .claim("role", user.getRole().getName().name())
        .setIssuedAt(now)
        .setExpiration(validity)
        .signWith(key, SignatureAlgorithm.HS256)
        .compact();
  }

  // 토큰 유효성 및 만료일자 확인
  public boolean validateToken(String token) {
    try {
      Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  // 토큰 userId 추출
  public Long getUserIdFromToken(String token) {
    Claims claims = Jwts.parserBuilder().setSigningKey(key).build()
        .parseClaimsJws(token).getBody();
    return Long.valueOf(claims.getSubject());
  }

  // 남은 만료기간
  public long getRemainMillisecond(String token){
    Date expiration = Jwts.parserBuilder().setSigningKey(key).build()
        .parseClaimsJws(token).getBody().getExpiration();
    return expiration.getTime() - (new Date()).getTime();
  }
}
