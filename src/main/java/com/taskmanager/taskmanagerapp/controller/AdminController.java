package com.taskmanager.taskmanagerapp.controller;

import com.taskmanager.taskmanagerapp.dto.response.ApiResponseDTO;
import com.taskmanager.taskmanagerapp.dto.request.UpdateUserRequestDTO;
import com.taskmanager.taskmanagerapp.dto.response.PaginatedResponseDTO;
import com.taskmanager.taskmanagerapp.dto.response.UserResponseDTO;
import com.taskmanager.taskmanagerapp.service.AccountLockoutService;
import com.taskmanager.taskmanagerapp.service.RateLimitService;
import com.taskmanager.taskmanagerapp.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
@Tag(name = "Admin - User Management", description = "Admin endpoints for managing users in the system")
public class AdminController {

    private final UserService userService;
    private final RateLimitService rateLimitService;
    private final AccountLockoutService accountLockoutService;

    @GetMapping("/users")
    public ResponseEntity<PaginatedResponseDTO<UserResponseDTO>> getAllUsers(@PageableDefault(size = 10,sort = "createdAt", direction = Sort.Direction.DESC)Pageable pageable){
        PaginatedResponseDTO<UserResponseDTO> response = userService.getAllUsers(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id){
        UserResponseDTO response = userService.getUserById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/users/role/{role}")
    @Operation(summary = "Get users by role with pagination")
    public ResponseEntity<PaginatedResponseDTO<UserResponseDTO>> getUsersByRole(
            @Parameter(description = "Role: USER or ADMIN", example = "USER") @PathVariable String role,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        PaginatedResponseDTO<UserResponseDTO> users = userService.getUsersByRole(role, pageable);
        return ResponseEntity.ok(users);
    }

    // search users by keyword
    @GetMapping("/users/search")
    @Operation(summary = "Search users by keyword", description = "Searches username, fullName, and email")
    public ResponseEntity<PaginatedResponseDTO<UserResponseDTO>> searchUsers(
            @Parameter(description = "Search keyword", example = "john") @RequestParam String keyword,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        PaginatedResponseDTO<UserResponseDTO> users = userService.searchUsers(keyword, pageable);
        return ResponseEntity.ok(users);
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

    @GetMapping("/rate-limit/{identifier}")
    @Operation(summary = "Check rate limit status", description = "Get remaining tokens for an IP/identifier")
    public ResponseEntity<Map<String, Object>> checkRateLimit(
            @Parameter(description = "IP address or identifier", example = "192.168.1.1")
            @PathVariable String identifier) {
        long availableTokens = rateLimitService.getAvailableTokens(identifier);
        return ResponseEntity.ok(Map.of(
                "identifier", identifier,
                "availableTokens", availableTokens,
                "message", availableTokens > 0 ? "Within rate limit" : "Rate limit exceeded"
        ));
    }

    @DeleteMapping("/rate-limit/{identifier}")
    @Operation(summary = "Reset rate limit", description = "Clear rate limit for specific identifier")
    public ResponseEntity<ApiResponseDTO> resetRateLimit(
            @Parameter(description = "IP address or identifier to reset", example = "192.168.1.1")
            @PathVariable String identifier) {
        rateLimitService.resetLimit(identifier);
        return ResponseEntity.ok(ApiResponseDTO.success("Rate limit reset for: " + identifier));
    }

    @DeleteMapping("/rate-limit")
    @Operation(summary = "Clear all rate limits", description = "Reset all rate limit buckets (use with caution)")
    public ResponseEntity<ApiResponseDTO> clearAllRateLimits() {
        rateLimitService.clearAll();
        return ResponseEntity.ok(ApiResponseDTO.success("All rate limits cleared"));
    }

    @GetMapping("/lockout/{username}")
    @Operation(summary = "Check account lockout status", description = "Get lockout details for a user")
    public ResponseEntity<AccountLockoutService.LockoutStatus> getLockoutStatus(
            @Parameter(description = "Username to check", example = "testuser")
            @PathVariable String username) {
        AccountLockoutService.LockoutStatus status = accountLockoutService.getLockoutStatus(username);
        return ResponseEntity.ok(status);
    }

    @PostMapping("/unlock/{username}")
    @Operation(summary = "Manually unlock account", description = "Remove lockout and reset failed attempts")
    public ResponseEntity<ApiResponseDTO> unlockAccount(
            @Parameter(description = "Username to unlock", example = "testuser")
            @PathVariable String username) {
        accountLockoutService.manuallyUnlockAccount(username);
        return ResponseEntity.ok(ApiResponseDTO.success("Account unlocked: " + username));
    }

    @PostMapping("/reset-attempts/{username}")
    @Operation(summary = "Reset failed login attempts", description = "Reset attempt counter without unlocking")
    public ResponseEntity<ApiResponseDTO> resetFailedAttempts(
            @Parameter(description = "Username to reset", example = "testuser")
            @PathVariable String username) {
        accountLockoutService.resetFailedAttempts(username);
        return ResponseEntity.ok(ApiResponseDTO.success("Failed attempts reset for: " + username));
    }


}
