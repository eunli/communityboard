package com.community.communityboard.config;

import com.community.communityboard.domain.Role;
import com.community.communityboard.domain.enums.RoleType;
import com.community.communityboard.repository.RoleRepository;
import java.util.Optional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class RoleInitializer implements CommandLineRunner {

  private final RoleRepository roleRepository;

  public RoleInitializer(RoleRepository roleRepository) {
    this.roleRepository = roleRepository;
  }

  @Override
  public void run(String... args) throws Exception {
    Optional<Role> userRole = roleRepository.findByName(RoleType.USER);
    if (userRole.isEmpty()) {
      Role role = new Role();
      role.setName(RoleType.USER);
      roleRepository.save(role);
    }

    Optional<Role> adminRole = roleRepository.findByName(RoleType.ADMIN);
    if (adminRole.isEmpty()) {
      Role role = new Role();
      role.setName(RoleType.ADMIN);
      roleRepository.save(role);
    }
  }
}

