package com.taskmanager.taskmanagerapp.config;

import com.taskmanager.taskmanagerapp.entity.UserDetails;
import com.taskmanager.taskmanagerapp.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // Create default admin
            UserDetails admin = new UserDetails();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setEmail("admin@example.com");
            admin.setFullname("System Administrator");
            admin.setRole("ADMIN");
            admin.setEnable(true);
            userRepository.save(admin);

            // Create default user
            UserDetails user = new UserDetails();
            user.setUsername("user");
            user.setPassword(passwordEncoder.encode("user123"));
            user.setEmail("user@example.com");
            user.setFullname("Regular User");
            user.setRole("USER");
            user.setEnable(true);
            userRepository.save(user);

            System.out.println("Default users created:");
            System.out.println("Admin - username: admin, password: admin123");
            System.out.println("User - username: user, password: user123");
        };
    }
}
