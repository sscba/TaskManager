package com.taskmanager.taskmanagerapp.controller;

import com.taskmanager.taskmanagerapp.dto.AuthResponseDTO;
import com.taskmanager.taskmanagerapp.dto.LoginRequestDTO;
import com.taskmanager.taskmanagerapp.dto.RegisterRequestDTO;
import com.taskmanager.taskmanagerapp.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> registerUser(@Valid @RequestBody RegisterRequestDTO request){
        AuthResponseDTO response = authService.register(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/login")
    public ResponseEntity<AuthResponseDTO> registerUser(@Valid @RequestBody LoginRequestDTO request){
        AuthResponseDTO response = authService.login(request);
        return ResponseEntity.ok(response);
    }

}
