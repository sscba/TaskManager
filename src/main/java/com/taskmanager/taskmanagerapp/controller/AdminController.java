package com.taskmanager.taskmanagerapp.controller;

import com.taskmanager.taskmanagerapp.entity.UserDetails;
import com.taskmanager.taskmanagerapp.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private UserRepository userRepository;

    public AdminController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserDetails>> getAllUsers(){
        List<UserDetails> users = userRepository.findAll();
        users.forEach(u -> u.setPassword("***"));
        return ResponseEntity.ok(users);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserDetails> getUserById(@PathVariable Long id){
        return userRepository.findById(id)
                .map(user -> {
                    user.setPassword("***");
                    return ResponseEntity.ok(user);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id){
        if(!userRepository.existsById(id)){
            return ResponseEntity.notFound().build();
        }
        userRepository.deleteById(id);
        return ResponseEntity.ok("User deleted successfully");
    }

    @GetMapping("/users/role/{role}")
    public ResponseEntity<List<UserDetails>> getUsersByRole(@PathVariable String role){
        List<UserDetails> users = userRepository.findByRole(role.toUpperCase());
        users.forEach(u->u.setPassword("***"));
        return ResponseEntity.ok(users);
    }
}
