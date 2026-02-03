package com.taskmanager.taskmanagerapp.config;

import com.taskmanager.taskmanagerapp.entity.Role;
import com.taskmanager.taskmanagerapp.entity.User;
import com.taskmanager.taskmanagerapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository) {
        return args -> {
            if (userRepository.count() == 0) {
                User admin = User.builder()
                        .username("admin")
                        .password(passwordEncoder.encode("admin123"))
                        .email("admin@example.com")
                        .fullName("System Administrator")
                        .role(Role.ADMIN)
                        .enabled(true)
                        .emailVerified(true)
                        .build();
                userRepository.save(admin);

                User user = User.builder()
                        .username("user")
                        .password(passwordEncoder.encode("user123"))
                        .email("user@example.com")
                        .fullName("Regular User")
                        .role(Role.USER)
                        .enabled(true)
                        .emailVerified(true)
                        .build();
                userRepository.save(user);

                System.out.println("=".repeat(50));
                System.out.println("Default users created:");
                System.out.println("Admin - username: admin, password: admin123");
                System.out.println("User  - username: user, password: user123");
                System.out.println("=".repeat(50));
            }
        };
    }
}
