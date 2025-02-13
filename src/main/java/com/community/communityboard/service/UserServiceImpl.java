package com.community.communityboard.service;

import com.community.communityboard.domain.Role;
import com.community.communityboard.domain.User;
import com.community.communityboard.domain.enums.RoleType;
import com.community.communityboard.domain.enums.UserStatus;
import com.community.communityboard.dto.user.EmailVerifyRequestDto;
import com.community.communityboard.dto.user.LoginRequestDto;
import com.community.communityboard.dto.user.SendEmailRequestDto;
import com.community.communityboard.dto.user.SignupRequestDto;
import com.community.communityboard.dto.user.TokenResponseDto;
import com.community.communityboard.dto.user.UpdateUserRequestDto;
import com.community.communityboard.dto.user.UserResponseDto;
import com.community.communityboard.exception.CustomException;
import com.community.communityboard.exception.ErrorCode;
import com.community.communityboard.repository.RoleRepository;
import com.community.communityboard.repository.UserRepository;
import com.community.communityboard.security.JwtTokenProvider;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtTokenProvider jwtTokenProvider;
  private final StringRedisTemplate redisTemplate;
  private final MailSender mailSender;

  private User findUserById(Long id) {
    return userRepository.findById(id)
        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
  }

  // 이메일 인증 전 단계 - 이메일 중복체크, 인증메일 발송, Redis에 코드 저장
  @Override
  public void sendVerificationEmail(SendEmailRequestDto requestDto) {
    String email = requestDto.getEmail();
    if (userRepository.findByEmail(email).isPresent()) {
      throw new CustomException(ErrorCode.EMAIL_ALREADY_EXISTS);
    }

    String verificationCode = UUID.randomUUID().toString().substring(0, 8);

    ValueOperations<String, String> ops = redisTemplate.opsForValue();
    ops.set("EMAIL_VERIF:" + email, verificationCode, Duration.ofMinutes(5));

    SimpleMailMessage mailMessage = new SimpleMailMessage();
    mailMessage.setTo(email);
    mailMessage.setSubject("[Communityboard] 이메일 인증 코드 안내");
    mailMessage.setText("인증코드는 " + verificationCode + " 입니다.");
    mailSender.send(mailMessage);
  }

  // 이메일 인증 코드 검증
  @Override
  public boolean verifyEmailCode(EmailVerifyRequestDto requestDto) {
    String key = "EMAIL_VERIF:" + requestDto.getEmail();
    ValueOperations<String, String> ops = redisTemplate.opsForValue();
    String savedCode = ops.get(key);
    if (savedCode != null && savedCode.equals(requestDto.getVerificationCode())) {
      redisTemplate.delete(key);
      return true;
    }
    return false;
  }

  // 회원 가입
  @Override
  public UserResponseDto registerUser(SignupRequestDto requestDto) {
    Role role = roleRepository.findByName(RoleType.USER)
        .orElseThrow(() -> new CustomException(ErrorCode.USER_ROLE_NOT_FOUND));

    if (userRepository.findByEmail(requestDto.getEmail()).isPresent()) {
      throw new CustomException(ErrorCode.EMAIL_ALREADY_EXISTS);
    }
    if (userRepository.findByNickname(requestDto.getNickname()).isPresent()) {
      throw new CustomException(ErrorCode.NICKNAME_ALREADY_EXISTS);
    }

    User user = User.builder()
        .email(requestDto.getEmail())
        .nickname(requestDto.getNickname())
        .password(passwordEncoder.encode(requestDto.getPassword()))
        .role(role)
        .build();

    userRepository.save(user);

    return UserResponseDto.builder()
        .id(user.getId())
        .email(user.getEmail())
        .nickname(user.getNickname())
        .build();
  }

  // 로그인 - AccessToken, RefreshToken 반환
  @Override
  public TokenResponseDto login(LoginRequestDto requestDto) {
    User user = userRepository.findByEmail(requestDto.getEmail())
        .orElseThrow(() -> new CustomException(ErrorCode.WRONG_EMAIL_OR_PASSWORD));

    if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
      throw new CustomException(ErrorCode.WRONG_EMAIL_OR_PASSWORD);
    }

    if (user.getStatus() == UserStatus.DEACTIVATED) {
      throw new CustomException(ErrorCode.USER_DEACTIVATED);
    }

    String accessToken = jwtTokenProvider.createAccessToken(user);
    String refreshToken = jwtTokenProvider.createRefreshToken(user);

    String hashedRefreshToken = passwordEncoder.encode(refreshToken);

    // key "RT:유저아이디", value 리프레시토큰 -> 해시된 Refresh Token 저장
    ValueOperations<String, String> ops = redisTemplate.opsForValue();
    ops.set("RT:" + user.getId(), hashedRefreshToken, Duration.ofMillis(
        jwtTokenProvider.getRemainMillisecond(refreshToken)));

    return TokenResponseDto.builder()
        .accessToken(accessToken)
        .refreshToken(refreshToken)
        .build();
  }

  // 로그아웃 - Redis에 저장된 RefreshToken 제거
  @Override
  public void logout(Long userId) {
    redisTemplate.delete("RT:" + userId);
  }

  // 내 정보 조회
  @Override
  public UserResponseDto getMyInfo(Long userId) {
    User user = findUserById(userId);
    return UserResponseDto.builder()
        .id(user.getId())
        .email(user.getEmail())
        .nickname(user.getNickname())
        .status(user.getStatus())
        .role(user.getRole().getName())
        .createdAt(user.getCreatedAt())
        .updatedAt(user.getUpdatedAt())
        .build();
  }


  // 상대 회원 정보 조회
  @Override
  public UserResponseDto getUser(Long userId) {
    User user = findUserById(userId);
    return UserResponseDto.builder()
        .id(user.getId())
        .email(user.getEmail())
        .nickname(user.getNickname())
        .status(user.getStatus())
        .role(user.getRole().getName())
        .build();
  }

  // 전체 회원 조회 (관리자)
  @Override
  public Page<UserResponseDto> getAllUsers(Pageable pageable) {
    Page<User> userPage = userRepository.findAll(pageable);

    return userPage.map(user -> UserResponseDto.builder()
            .id(user.getId())
            .email(user.getEmail())
            .nickname(user.getNickname())
            .status(user.getStatus())
            .role(user.getRole().getName())
            .createdAt(user.getCreatedAt())
            .updatedAt(user.getUpdatedAt())
            .deletedAt(user.getDeletedAt())
            .build());
  }

  // 회원 정보 수정
  @Override
  public UserResponseDto updateUser(Long userId, UpdateUserRequestDto requestDto) {
    User user = findUserById(userId);

    if (requestDto.getNickname() != null && !requestDto.getNickname().isBlank()) {
      Optional<User> existingUser = userRepository.findByNickname(requestDto.getNickname());
      if (existingUser.isPresent() && !existingUser.get().getId().equals(userId)) {
        throw new CustomException(ErrorCode.NICKNAME_ALREADY_EXISTS);
      }
      user.setNickname(requestDto.getNickname());
    }

    if (requestDto.getPassword() != null
        && requestDto.getNewPassword() != null
        && !requestDto.getPassword().isBlank()
        && !requestDto.getNewPassword().isBlank()) {
      if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
        throw new CustomException(ErrorCode.WRONG_EMAIL_OR_PASSWORD);
      }
      user.setPassword(passwordEncoder.encode(requestDto.getNewPassword()));
    }

    userRepository.save(user);

    return UserResponseDto.builder()
        .id(user.getId())
        .email(user.getEmail())
        .nickname(user.getNickname())
        .status(user.getStatus())
        .role(user.getRole().getName())
        .build();
  }

  // 회원 탈퇴 - soft delete(status 변경, deletedAt 기록)
  @Override
  public void deleteUser(Long userId) {
    User user = findUserById(userId);
    user.setStatus(UserStatus.DEACTIVATED);
    user.setDeletedAt(LocalDateTime.now());
    userRepository.save(user);
  }

  // 액세스 토큰 재발급 (리프레시 토큰 함께 재발급)
  @Override
  public TokenResponseDto reissueTokens(String accessToken, String refreshToken) {
    // Access Token 유효성 검증 - 여전히 유효하다면 만료 전이므로 재발급 거부
    System.out.println(jwtTokenProvider.validateToken(accessToken));
    if (jwtTokenProvider.validateToken(accessToken)) {
      throw new CustomException(ErrorCode.ACCESS_TOKEN_NOT_EXPIRED_YET);
    }

    // Refresh Token 유효성 검증
    if (!jwtTokenProvider.validateToken(refreshToken)) {
      throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
    }

    Long userId = jwtTokenProvider.getUserIdFromToken(refreshToken);

    // Redis에 저장된 리프레시 토큰 일치 확인 (일치하지 않을 시 탈취 위험으로 Redis에서 토큰 삭제)
    String StoredHashedRt = redisTemplate.opsForValue().get("RT:" + userId);
    if (StoredHashedRt == null || !passwordEncoder.matches(refreshToken, StoredHashedRt)) {
      redisTemplate.delete("RT:" + userId);
      throw new CustomException(ErrorCode.REFRESH_TOKEN_MISMATCH);
    }

    User user = findUserById(userId);

    String newAccessToken = jwtTokenProvider.createAccessToken(user);
    String newRefreshToken = jwtTokenProvider.createRefreshToken(user);

    String newHashedRt = passwordEncoder.encode(newRefreshToken);
    redisTemplate.opsForValue()
        .set("RT:" + userId, newHashedRt, Duration.ofMillis(jwtTokenProvider.getRemainMillisecond(newRefreshToken)));

    return TokenResponseDto.builder()
        .accessToken(newAccessToken)
        .refreshToken(newRefreshToken)
        .build();
  }
}
