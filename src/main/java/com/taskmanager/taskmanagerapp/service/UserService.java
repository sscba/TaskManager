package com.taskmanager.taskmanagerapp.service;

import com.taskmanager.taskmanagerapp.dto.request.UpdateUserRequestDTO;
import com.taskmanager.taskmanagerapp.dto.response.PaginatedResponseDTO;
import com.taskmanager.taskmanagerapp.dto.response.UserResponseDTO;
import com.taskmanager.taskmanagerapp.entity.Role;
import com.taskmanager.taskmanagerapp.entity.User;
import com.taskmanager.taskmanagerapp.exception.DuplicateResourceException;
import com.taskmanager.taskmanagerapp.exception.ResourceNotFoundException;
import com.taskmanager.taskmanagerapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public PaginatedResponseDTO<UserResponseDTO> getAllUsers(Pageable pageable) {
        log.info("Fetching all users, page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());
        Page<User> usersPage = userRepository.findAll(pageable);
        // map() transforms every User entity inside the Page into a UserResponseDTO
        Page<UserResponseDTO> dtosPage = usersPage.map(this::convertToDTO);
        return PaginatedResponseDTO.of(dtosPage);
    }

    @Transactional(readOnly = true)
    public UserResponseDTO getUserById(Long id) {
        log.info("Fetching user by id: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return convertToDTO(user);
    }

    @Transactional(readOnly = true)
    public PaginatedResponseDTO<UserResponseDTO> getUsersByRole(String role,Pageable pageable) {
        log.info("Fetching users by role: {}, page: {}", role, pageable.getPageNumber());
        try {
            Role roleEnum = Role.valueOf(role.toUpperCase());
            Page<User> usersPage = userRepository.findByRole(roleEnum, pageable);
            Page<UserResponseDTO> dtosPage = usersPage.map(this::convertToDTO);
            return PaginatedResponseDTO.of(dtosPage);
        } catch (IllegalArgumentException e) {
            throw new ResourceNotFoundException("Invalid role: " + role);
        }
    }

    @Transactional(readOnly = true)
    public PaginatedResponseDTO<UserResponseDTO> searchUsers(String keyword, Pageable pageable) {
        log.info("Searching users with keyword: {}", keyword);
        Page<User> usersPage = userRepository.searchUsers(keyword, pageable);
        Page<UserResponseDTO> dtosPage = usersPage.map(this::convertToDTO);
        return PaginatedResponseDTO.of(dtosPage);
    }

    @Transactional
    public UserResponseDTO updateUser(Long id, UpdateUserRequestDTO request) {
        log.info("Updating user: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        // Update email if provided
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new DuplicateResourceException("Email already exists: " + request.getEmail());
            }
            user.setEmail(request.getEmail());
        }

        // Update full name if provided
        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }

        // Update password if provided
        if (request.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        User updatedUser = userRepository.save(user);
        log.info("User updated successfully: {}", updatedUser.getId());

        return convertToDTO(updatedUser);
    }

    @Transactional
    public void deleteUser(Long id) {
        log.info("Deleting user: {}", id);

        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }

        userRepository.deleteById(id);
        log.info("User deleted successfully: {}", id);
    }

    private UserResponseDTO convertToDTO(User user) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole().name())
                .enabled(user.getEnabled())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
