package com.community.communityboard.security;

import com.community.communityboard.domain.User;
import com.community.communityboard.domain.enums.UserStatus;
import java.util.Collections;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
public class CustomUserDetails implements UserDetails {

  private final User user;

  public CustomUserDetails(User user) {
    this.user = user;
  }

  @Override
  public String getUsername() {
    return user.getEmail();
  }
  @Override
  public String getPassword() {
    return user.getPassword();
  }

  @Override
  public boolean isAccountNonExpired() { return true; }
  @Override
  public boolean isAccountNonLocked() { return true; }
  @Override
  public boolean isCredentialsNonExpired() { return true; }
  @Override
  public boolean isEnabled() {
    return user.getStatus() == UserStatus.ACTIVE;
  }
  @Override
  public java.util.Collection<? extends GrantedAuthority> getAuthorities() {
    return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().getName().name()));
  }
}
