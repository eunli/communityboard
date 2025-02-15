package com.community.communityboard;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.community.communityboard.domain.Role;
import com.community.communityboard.domain.User;
import com.community.communityboard.domain.enums.RoleType;
import com.community.communityboard.domain.enums.UserStatus;
import com.community.communityboard.repository.RoleRepository;
import com.community.communityboard.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@DataJpaTest
@Transactional
public class UserRepositoryTest {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private RoleRepository roleRepository;

  private Role defaultRole;

  @BeforeEach
  void setUp() {
    defaultRole = roleRepository.findByName(RoleType.USER)
        .orElseGet(() -> {
          Role newRole = new Role();
          newRole.setName(RoleType.USER);
          return roleRepository.save(newRole);
        });
  }

  @Test
  void findByEmail_shouldReturnUser() {
    // given
    User user = new User();
    user.setEmail("test@test.com");
    user.setNickname("tester");
    user.setPassword("password123");
    user.setStatus(UserStatus.ACTIVE);
    user.setRole(defaultRole);
    userRepository.save(user);

    // when
    Optional<User> foundUser = userRepository.findByEmail("test@test.com");

    // then
    assertThat(foundUser).isPresent();
    assertThat(foundUser.get().getNickname()).isEqualTo("tester");
  }

  @Test
  void findByNickname_shouldReturnUser() {
    // given
    User user = new User();
    user.setEmail("test2@test.com");
    user.setNickname("tester2");
    user.setPassword("password123");
    user.setStatus(UserStatus.ACTIVE);
    user.setRole(defaultRole);
    userRepository.save(user);

    // when
    Optional<User> foundUser = userRepository.findByNickname("tester2");

    // then
    assertThat(foundUser).isPresent();
    assertThat(foundUser.get().getEmail()).isEqualTo("test2@test.com");
  }
}
