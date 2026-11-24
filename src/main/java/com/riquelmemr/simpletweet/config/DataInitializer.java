package com.riquelmemr.simpletweet.config;

import com.riquelmemr.simpletweet.enums.RoleEnum;
import com.riquelmemr.simpletweet.model.Role;
import com.riquelmemr.simpletweet.model.User;
import com.riquelmemr.simpletweet.repository.RoleRepository;
import com.riquelmemr.simpletweet.repository.UserRepository;
import com.riquelmemr.simpletweet.service.user.UserService;
import java.util.Objects;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        Role adminRole = roleRepository.findByName(RoleEnum.ADMIN.name().toUpperCase());
        User adminUser = userService.findByUsername("admin");

        if (Objects.nonNull(adminUser)) {
            return;
        }

        User user = new User();
        user.setUsername("admin");
        user.setEmail("admin@admin.com");
        user.setPassword(passwordEncoder.encode("nimda"));
        user.setRoles(Set.of(adminRole));
        userRepository.save(user);

        log.info("Admin user [{}] created", user.getUsername());
    }
}
