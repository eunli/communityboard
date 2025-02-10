package com.community.communityboard.domain;

import com.community.communityboard.domain.enums.UserStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class User extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String email;

  @Column(nullable = false)
  private String password;

  @Column(nullable = false)
  private String nickname;

  @ManyToOne
  @JoinColumn(name = "role_id", nullable = false)
  private Role role;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private UserStatus status = UserStatus.ACTIVE;

  private LocalDateTime deletedAt;

  @Builder
  public User(String email, String password, String nickname, Role role) {
    this.email = email;
    this.password = password;
    this.nickname = nickname;
    this.role = role;
  }
}
