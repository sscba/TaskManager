package com.taskmanager.taskmanagerapp.controller;

import com.taskmanager.taskmanagerapp.dto.ApiResponseDTO;
import com.taskmanager.taskmanagerapp.dto.UpdateUserRequestDTO;
import com.taskmanager.taskmanagerapp.dto.UserResponseDTO;
import com.taskmanager.taskmanagerapp.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private UserService userService;

    @GetMapping("/users")
    public ResponseEntity<List<UserResponseDTO>> getAllUsers(){
        List<UserResponseDTO> response = userService.getAllUsers();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id){
        UserResponseDTO response = userService.getUserById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/users/role/{role}")
    public ResponseEntity<List<UserResponseDTO>> getUserById(@PathVariable String role){
        List<UserResponseDTO> response = userService.getUsersByRole(role);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable Long id, @Valid @RequestBody UpdateUserRequestDTO request){
        UserResponseDTO response = userService.updateUser(id,request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<ApiResponseDTO> deleteUser(@PathVariable Long id){
        userService.deleteUser(id);
        return ResponseEntity.ok(ApiResponseDTO.success("User deleted successfully"));
    }
}
