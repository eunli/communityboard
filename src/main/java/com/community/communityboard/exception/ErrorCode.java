package com.community.communityboard.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
  // User
  EMAIL_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "이미 존재하는 이메일입니다."),
  NICKNAME_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "이미 존재하는 닉네임입니다."),
  WRONG_EMAIL_OR_PASSWORD(HttpStatus.BAD_REQUEST, "잘못된 이메일 또는 비밀번호입니다."),
  USER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 회원입니다."),
  USER_DEACTIVATED(HttpStatus.BAD_REQUEST, "탈퇴 처리된 회원입니다."),
  ACCESS_TOKEN_NOT_EXPIRED_YET(HttpStatus.BAD_REQUEST, "AccessToken이 만료되지 않았습니다."),
  INVALID_REFRESH_TOKEN(HttpStatus.BAD_REQUEST, "유효하지 않은 RefreshToken 입니다."),
  REFRESH_TOKEN_MISMATCH(HttpStatus.UNAUTHORIZED, "RefreshToken 불일치. 재로그인이 필요합니다."),

  // Role
  USER_ROLE_NOT_FOUND(HttpStatus.INTERNAL_SERVER_ERROR, "User Role이 존재하지 않습니다.");

  private final HttpStatus status;
  private final String message;

  ErrorCode(HttpStatus status, String message) {
    this.status = status;
    this.message = message;
  }

  public HttpStatus getStatus() {
    return status;
  }

  public String getMessage() {
    return message;
  }
}
