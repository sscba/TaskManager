package com.taskmanager.taskmanagerapp.controller;

import com.taskmanager.taskmanagerapp.dto.UserRegistrationDTO;
import com.taskmanager.taskmanagerapp.entity.UserDetails;
import com.taskmanager.taskmanagerapp.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserRegistrationDTO dto){
        if(userRepository.existsByUsername(dto.getUsername())){
            return ResponseEntity.badRequest().body("Username already exists");
        }

        UserDetails user = new UserDetails();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setEmail(dto.getEmail());
        user.setFullname(dto.getFullname());
        user.setRole(dto.getRole() != null ? dto.getRole() : "USER");
        user.setEnable(true);

        userRepository.save(user);
        return ResponseEntity.ok("User registered successfully");
    }
}
