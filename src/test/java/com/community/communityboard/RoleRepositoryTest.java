package com.community.communityboard;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.community.communityboard.domain.Role;
import com.community.communityboard.domain.enums.RoleType;
import com.community.communityboard.repository.RoleRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
public class RoleRepositoryTest {

  @Autowired
  private RoleRepository roleRepository;

  @Test
  void userRoleShouldExist() {
    Optional<Role> userRole = roleRepository.findByName(RoleType.USER);
    assertThat(userRole).isPresent();
  }

  @Test
  void adminRoleShouldExist() {
    Optional<Role> adminRole = roleRepository.findByName(RoleType.ADMIN);
    assertThat(adminRole).isPresent();
  }
}
